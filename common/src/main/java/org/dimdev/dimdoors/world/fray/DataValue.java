package org.dimdev.dimdoors.world.fray;

import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface DataValue<T> {
    T get(Object object);
    T getOrCreate(Object object);
    void set(Object object, T value);
    void update(Object object, T defaultValue, UnaryOperator<T> operator);
    void update(Object object, UnaryOperator<T> operator);
    void remove(Object object);
    boolean has(Object object);
}
