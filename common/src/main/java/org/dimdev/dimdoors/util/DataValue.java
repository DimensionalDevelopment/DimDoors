package org.dimdev.dimdoors.util;

import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public interface DataValue<T> {
    @Nullable T get(Object object);
    T getOrCreate(Object object);
    void set(Object object, T value);
    void update(Object object, T defaultValue, UnaryOperator<T> operator);
    void update(Object object, UnaryOperator<T> operator);
    void remove(Object object);
    boolean has(Object object);
}
