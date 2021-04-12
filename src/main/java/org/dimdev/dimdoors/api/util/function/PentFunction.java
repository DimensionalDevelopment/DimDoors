package org.dimdev.dimdoors.api.util.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface PentFunction<T, U, V, W, X, R> {

	R apply(T t, U u, V v, W w, X x);

	default <A> PentFunction<T, U, V, W, X, A> andThen(Function<R, A> after) {
		Objects.requireNonNull(after);
		return (T t, U u, V v, W w, X x) -> after.apply(apply(t, u, v, w, x));
	}
}
