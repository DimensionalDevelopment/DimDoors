package org.dimdev.dimdoors.api.util.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface HexFunction<T, U, V, W, X, Y, R> {

	R apply(T t, U u, V v, W w, X x, Y y);

	default <A> HexFunction<T, U, V, W, X, Y, A> andThen(Function<R, A> after) {
		Objects.requireNonNull(after);
		return (T t, U u, V v, W w, X x, Y y) -> after.apply(apply(t, u, v, w, x, y));
	}
}
