package org.dimdev.dimdoors.client.config;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;

public final class ObjectControl<T> extends AbstractConfigControl<T> {
    private T value;
    private final List<ConfigControl<?>> children = new ArrayList<>();
    private boolean expanded = true;

    public ObjectControl(ConfigControlRegistry registry, OptionInfo<T> option) {
        super(registry, option);
        this.value = option.value();

        if (this.value == null) {
            this.value = this.createDefault(option.type());
        }

        this.rebuildChildren();
    }

    @Override
    public int rowCount() {
        if (!this.expanded) {
            return 1;
        }

        int rows = 1;

        for (ConfigControl<?> child : this.children) {
            rows += child.rowCount();
        }

        return rows;
    }

    @Override
    public void addRows(ControlHost host, RowCursor cursor) {
        this.addHeaderRow(host, cursor);

        if (!this.expanded) {
            return;
        }

        for (ConfigControl<?> child : this.children) {
            child.addRows(host, cursor);
        }
    }

    @Override
    public boolean validate() {
        boolean valid = this.value != null;

        for (ConfigControl<?> child : this.children) {
            valid &= child.validate();
        }

        return valid;
    }

    @Override
    public T pendingValue() {
        return this.value;
    }

    @Override
    public void commit() {
        for (ConfigControl<?> child : this.children) {
            child.commit();
        }

        this.option.setValue(this.value);
    }

    private void addHeaderRow(ControlHost host, RowCursor cursor) {
        int y = cursor.next();

        if (!cursor.isVisible(y)) {
            return;
        }

        host.addLabel(this.option.label(host), host.labelX(), y + 6, 0xE0E0E0);

        Component message = Component.literal((this.expanded ? "Collapse" : "Expand") + " object");

        Button button = Button.builder(message, ignored -> {
                    this.expanded = !this.expanded;
                    host.rebuild();
                })
                .bounds(host.valueX(), y, host.valueWidth(), 20)
                .build();

        host.addTooltip(button, this.option.tooltipKey());
        host.addWidget(button);
    }

    private void rebuildChildren() {
        this.children.clear();

        if (this.value == null) {
            return;
        }

        List<OptionInfo<?>> options = new ArrayList<>();

        ConfigReflection.scanOptions(
                this.option.key(),
                this.value,
                options,
                Collections.newSetFromMap(new IdentityHashMap<>())
        );

        for (OptionInfo<?> child : options) {
            this.children.add(this.registry.createCaptured(child));
        }
    }

    private T createDefault(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Cannot create config object: " + type.getName(), e);
        }
    }
}
