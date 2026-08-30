package org.dimdev.dimcore.util;

import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public interface DataValue<T> {
    @Nullable T get(Object object);
    default T getOrDefault(Object object, T value) {
        var t = get(object);
        return t != null ? t : value;
    }
    T getOrCreate(Object object);
    void set(Object object, T value);
    void update(Object object, T defaultValue, UnaryOperator<T> operator);
    void update(Object object, UnaryOperator<T> operator);
    void remove(Object object);
    boolean has(Object object);
}
