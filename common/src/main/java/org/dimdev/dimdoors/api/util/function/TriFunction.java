package org.dimdev.dimdoors.api.util.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface TriFunction<T, U, V, R> {

	R apply(T t, U u, V v);

	default <W> TriFunction<T, U, V, W> andThen(Function<R, W> after) {
		Objects.requireNonNull(after);
		return (T t, U u, V v) -> after.apply(apply(t, u, v));
	}
}
