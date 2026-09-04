package org.dimdev.dimcore.api.util.function;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamUtils {
    public static <T, A, R> Collector<T, A, Void> consuming(Collector<T, A, R> downstream, Consumer<R> action) {
        return Collectors.collectingAndThen(downstream, r -> {
            action.accept(r);
            return null;
        });
    }

    public static IntStream toIntStream(Stream<Integer> stream) {
        return stream.mapToInt(Integer::intValue);
    }
}
