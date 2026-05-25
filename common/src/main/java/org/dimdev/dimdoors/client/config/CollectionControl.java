package org.dimdev.dimdoors.client.config;

import com.google.common.reflect.TypeToken;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public final class CollectionControl<E, C extends Collection<E>> extends AbstractConfigControl<C> {
    private final TypeToken<E> elementType;
    private final Class<E> elementClass;
    private final Supplier<C> collectionFactory;
    private final boolean set;
    private final List<Entry<E>> entries = new ArrayList<>();
    private boolean expanded;

    private CollectionControl(
            ConfigControlRegistry registry,
            OptionInfo<C> option,
            TypeToken<E> elementType,
            Supplier<C> collectionFactory,
            boolean set
    ) {
        super(registry, option);
        this.elementType = elementType;
        this.elementClass = this.rawClass(elementType);
        this.collectionFactory = collectionFactory;
        this.set = set;

        C collection = option.value();

        if (collection != null) {
            int index = 0;

            for (E entry : collection) {
                this.entries.add(this.createEntry(index++, entry));
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static ConfigControl<Collection> create(ConfigControlRegistry registry, OptionInfo<Collection> option) {
        TypeToken elementType = CollectionElementTypes.resolve(option);

        return new CollectionControl(
                registry,
                option,
                elementType,
                () -> createCollection(option),
                isSet(option)
        );
    }

    @Override
    public int rowCount() {
        if (!this.expanded) {
            return 1;
        }

        int rows = 2;

        for (Entry<E> entry : this.entries) {
            rows += 1 + entry.control.rowCount();
        }

        return rows;
    }

    @Override
    public void addRows(ControlHost host, RowCursor cursor) {
        this.addHeaderRow(host, cursor);

        if (!this.expanded) {
            return;
        }

        for (int i = 0; i < this.entries.size(); i++) {
            this.addEntryHeaderRow(host, cursor, i);
            this.entries.get(i).control.addRows(host, cursor);
        }

        this.addAddRow(host, cursor);
    }

    @Override
    public boolean validate() {
        boolean valid = true;
        Set<E> seen = new HashSet<>();

        for (Entry<E> entry : this.entries) {
            boolean entryValid = entry.control.validate();

            if (entryValid && this.set) {
                E value = entry.control.pendingValue();

                if (!seen.add(value)) {
                    entryValid = false;
                }
            }

            valid &= entryValid;
        }

        return valid;
    }

    @Override
    public C pendingValue() {
        C result = this.collectionFactory.get();

        for (Entry<E> entry : this.entries) {
            result.add(entry.control.pendingValue());
        }

        return result;
    }

    @Override
    public void commit() {
        this.option.setValue(this.pendingValue());
    }

    private void addHeaderRow(ControlHost host, RowCursor cursor) {
        int y = cursor.next();

        if (!cursor.isVisible(y)) {
            return;
        }

        host.addLabel(this.option.label(host), host.labelX(), y + 6, 0xE0E0E0);

        Component message = Component.literal((this.expanded ? "Collapse" : "Expand") + " (" + this.entries.size() + ")");

        Button button = Button.builder(message, ignored -> {
                    this.expanded = !this.expanded;
                    host.rebuild();
                })
                .bounds(host.valueX(), y, host.valueWidth(), 20)
                .build();

        host.addTooltip(button, this.option.tooltipKey());
        host.addWidget(button);
    }

    private void addEntryHeaderRow(ControlHost host, RowCursor cursor, int index) {
        int y = cursor.next();

        if (!cursor.isVisible(y)) {
            return;
        }

        host.addLabel(Component.literal("#" + (index + 1)), host.labelX() + 12, y + 6, 0xAAAAAA);

        Button remove = Button.builder(Component.literal("Remove"), ignored -> {
                    this.entries.remove(index);
                    this.rebuildEntryControls();
                    host.rebuild();
                })
                .bounds(host.valueX(), y, host.valueWidth(), 20)
                .build();

        host.addWidget(remove);
    }

    private void addAddRow(ControlHost host, RowCursor cursor) {
        int y = cursor.next();

        if (!cursor.isVisible(y)) {
            return;
        }

        Button add = Button.builder(Component.literal("+ Add"), ignored -> {
                    this.entries.add(this.createEntry(this.entries.size(), this.createDefaultElement()));
                    this.expanded = true;
                    host.rebuild();
                })
                .bounds(host.valueX(), y, host.valueWidth(), 20)
                .build();

        host.addWidget(add);
    }

    private Entry<E> createEntry(int index, E value) {
        Entry<E> entry = new Entry<>(value);
        entry.control = this.createEntryControl(index, entry);
        return entry;
    }

    private ConfigControl<E> createEntryControl(int index, Entry<E> entry) {
        String key = this.option.key() + "[" + index + "]";

        OptionInfo<E> entryOption = OptionInfo.value(
                key,
                this.elementType,
                () -> entry.value,
                value -> entry.value = value,
                Component.literal("#" + (index + 1)),
                this.option.tooltipKey()
        );

        return this.registry.create(entryOption);
    }

    private void rebuildEntryControls() {
        for (int i = 0; i < this.entries.size(); i++) {
            Entry<E> entry = this.entries.get(i);
            entry.control = this.createEntryControl(i, entry);
        }
    }

    private E createDefaultElement() {
        if (this.elementClass == String.class) {
            return this.elementClass.cast("");
        }

        if (this.elementClass == Integer.class) {
            return this.elementClass.cast(0);
        }

        if (this.elementClass == Float.class) {
            return this.elementClass.cast(0.0F);
        }

        if (this.elementClass == Double.class) {
            return this.elementClass.cast(0.0D);
        }

        if (this.elementClass == Boolean.class) {
            return this.elementClass.cast(false);
        }

        if (ResourceKey.class.isAssignableFrom(this.elementClass)) {
            return null;
        }

        if (this.elementClass.isEnum()) {
            E[] constants = this.elementClass.getEnumConstants();
            return constants.length == 0 ? null : constants[0];
        }

        if (ConfigReflection.isExpandableConfigObjectType(this.elementClass)) {
            return this.createDefaultObject(this.elementClass);
        }

        return null;
    }

    private E createDefaultObject(Class<E> type) {
        try {
            Constructor<E> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Cannot create collection element: " + type.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<E> rawClass(TypeToken<E> type) {
        return (Class<E>) type.getRawType();
    }

    private static boolean isSet(OptionInfo<? extends Collection> option) {
        Collection current = option.value();

        if (current != null) {
            return current instanceof Set<?>;
        }

        return Set.class.isAssignableFrom(option.type());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Collection createCollection(OptionInfo<? extends Collection> option) {
        Collection current = option.value();

        if (current instanceof LinkedList<?>) {
            return new LinkedList<>();
        }

        if (current instanceof HashSet<?>) {
            return new HashSet<>();
        }

        if (current instanceof Set<?>) {
            return new LinkedHashSet<>();
        }

        if (current instanceof List<?>) {
            return new ArrayList<>();
        }

        Class<?> type = current == null ? option.type() : current.getClass();

        if (Set.class.isAssignableFrom(type)) {
            return new LinkedHashSet<>();
        }

        if (List.class.isAssignableFrom(type) || Collection.class == type) {
            return new ArrayList<>();
        }

        if (!type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
            try {
                Constructor<?> constructor = type.getDeclaredConstructor();
                constructor.setAccessible(true);
                return (Collection) constructor.newInstance();
            } catch (ReflectiveOperationException ignored) {
            }
        }

        return new ArrayList<>();
    }

    private static final class Entry<E> {
        private E value;
        private ConfigControl<E> control;

        private Entry(E value) {
            this.value = value;
        }
    }
}
