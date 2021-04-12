package org.dimdev.dimdoors.api.util.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface SeptFunction<T, U, V, W, X, Y, Z, R> {

	R apply(T t, U u, V v, W w, X x, Y y, Z z);

	default <A> SeptFunction<T, U, V, W, X, Y, Z, A> andThen(Function<R, A> after) {
		Objects.requireNonNull(after);
		return (T t, U u, V v, W w, X x, Y y, Z z) -> after.apply(apply(t, u, v, w, x, y, z));
	}
}
