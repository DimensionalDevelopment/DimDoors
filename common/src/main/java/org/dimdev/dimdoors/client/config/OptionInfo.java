package org.dimdev.dimdoors.client.config;

import com.google.common.reflect.TypeToken;
import net.minecraft.network.chat.Component;
import org.dimdev.dimdoors.config.Option;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class OptionInfo<T> {
    private final String key;
    private final TypeToken<T> typeToken;
    private final Class<T> type;
    private final Field field;
    private final Object owner;
    private final Option annotation;
    private final Supplier<T> getter;
    private final Consumer<T> setter;
    private final Component labelOverride;
    private final String tooltipKey;

    private OptionInfo(
            String key,
            TypeToken<T> typeToken,
            Field field,
            Object owner,
            Option annotation,
            Supplier<T> getter,
            Consumer<T> setter,
            Component labelOverride,
            String tooltipKey
    ) {
        this.key = Objects.requireNonNull(key, "key");
        this.typeToken = normalize(Objects.requireNonNull(typeToken, "typeToken"));
        this.type = rawClass(this.typeToken);
        this.field = field;
        this.owner = owner;
        this.annotation = annotation;
        this.getter = Objects.requireNonNull(getter, "getter");
        this.setter = Objects.requireNonNull(setter, "setter");
        this.labelOverride = labelOverride;
        this.tooltipKey = tooltipKey == null ? key : tooltipKey;
    }

    @SuppressWarnings("unchecked")
    public static <T> OptionInfo<T> field(String key, Field field, Object owner, Option annotation) {
        Objects.requireNonNull(field, "field");
        Objects.requireNonNull(owner, "owner");
        field.setAccessible(true);

        TypeToken<T> typeToken = (TypeToken<T>) TypeToken.of(wrapPrimitive(field.getGenericType()));
        Class<T> rawType = rawClass(typeToken);

        return new OptionInfo<>(
                key,
                typeToken,
                field,
                owner,
                annotation,
                () -> rawType.cast(readField(key, field, owner)),
                value -> writeField(key, field, owner, value),
                null,
                key
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> OptionInfo<T> value(
            String key,
            Class<T> type,
            Supplier<T> getter,
            Consumer<T> setter,
            Component labelOverride,
            String tooltipKey
    ) {
        TypeToken<T> typeToken = (TypeToken<T>) TypeToken.of(wrapPrimitive(type));

        return value(
                key,
                typeToken,
                getter,
                setter,
                labelOverride,
                tooltipKey
        );
    }

    public static <T> OptionInfo<T> value(
            String key,
            TypeToken<T> typeToken,
            Supplier<T> getter,
            Consumer<T> setter,
            Component labelOverride,
            String tooltipKey
    ) {
        return new OptionInfo<>(
                key,
                typeToken,
                null,
                null,
                null,
                getter,
                setter,
                labelOverride,
                tooltipKey
        );
    }

    public String key() {
        return this.key;
    }

    public TypeToken<T> typeToken() {
        return this.typeToken;
    }

    public Class<T> type() {
        return this.type;
    }

    public Field field() {
        return this.field;
    }

    public boolean hasField() {
        return this.field != null;
    }

    public Object owner() {
        return this.owner;
    }

    public Option annotation() {
        return this.annotation;
    }

    public T value() {
        return this.getter.get();
    }

    public void setValue(T value) {
        this.setter.accept(value);
    }

    @SuppressWarnings("unchecked")
    public Class<? extends T> actualType() {
        T value = this.value();
        return value == null ? this.type : (Class<? extends T>) value.getClass();
    }

    public Component label(ControlHost host) {
        return this.labelOverride == null ? host.optionLabel(this.key) : this.labelOverride;
    }

    public String tooltipKey() {
        return this.tooltipKey;
    }

    private static Object readField(String key, Field field, Object owner) {
        try {
            return field.get(owner);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to read config option: " + key, e);
        }
    }

    private static void writeField(String key, Field field, Object owner, Object value) {
        try {
            field.set(owner, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to write config option: " + key, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> TypeToken<T> normalize(TypeToken<T> token) {
        return (TypeToken<T>) TypeToken.of(wrapPrimitive(token.getType()));
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> rawClass(TypeToken<T> token) {
        return (Class<T>) wrapPrimitive(token.getRawType());
    }

    private static java.lang.reflect.Type wrapPrimitive(java.lang.reflect.Type type) {
        if (type == boolean.class) {
            return Boolean.class;
        }

        if (type == byte.class) {
            return Byte.class;
        }

        if (type == short.class) {
            return Short.class;
        }

        if (type == int.class) {
            return Integer.class;
        }

        if (type == long.class) {
            return Long.class;
        }

        if (type == float.class) {
            return Float.class;
        }

        if (type == double.class) {
            return Double.class;
        }

        if (type == char.class) {
            return Character.class;
        }

        return type;
    }
}
