package org.dimdev.dimcore.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public final class SimpleEvent<T> {
    private final List<T> listeners = new ArrayList<>();
    private final T invoker;

    private SimpleEvent(Function<List<T>, T> invokerFactory) {
        this.invoker = invokerFactory.apply(listeners);
    }

    public static <T> SimpleEvent<T> of(Function<List<T>, T> factory) {
        return new SimpleEvent<>(factory);
    }

    public static <T> SimpleEvent<Consumer<T>> consumerLoop() {
        return new SimpleEvent<>(consumers -> value -> consumers.forEach(consumer -> consumer.accept(value)));
    }

    public void register(T listener) {
        listeners.add(listener);
    }

    public T invoker() {
        return invoker;
    }
}
