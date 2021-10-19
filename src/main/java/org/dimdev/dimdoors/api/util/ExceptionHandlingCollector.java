package org.dimdev.dimdoors.api.util;

import org.apache.logging.log4j.util.TriConsumer;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ExceptionHandlingCollector<T, A, R> implements Collector<T, A, R> {
	private final Collector<T, A, R> collector;
	private final TriConsumer<A, T, Exception> exceptionalAccumulator;

	public ExceptionHandlingCollector(Collector<T, A, R> collector, TriConsumer<A, T, Exception> exceptionalAccumulator) {
		this.collector = collector;
		this.exceptionalAccumulator = exceptionalAccumulator;
	}

	@Override
	public Supplier<A> supplier() {
		return collector.supplier();
	}

	@Override
	public BiConsumer<A, T> accumulator() {
		return (a, t) -> {
			try {
				collector.accumulator().accept(a, t);
			} catch (Exception e) {
				exceptionalAccumulator.accept(a, t, e);
			}
		};
	}

	@Override
	public BinaryOperator<A> combiner() {
		return collector.combiner();
	}

	@Override
	public Function<A, R> finisher() {
		return collector.finisher();
	}

	@Override
	public Set<Characteristics> characteristics() {
		return collector.characteristics();
	}
}
