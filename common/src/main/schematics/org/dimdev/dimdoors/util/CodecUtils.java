package org.dimdev.dimdoors.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import org.dimdev.dimdoors.api.util.Path;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class CodecUtils {
    public static <T, V, U extends Map<T, V>> Codec<U> listMap(Codec<T> keyCodec, Codec<V> valueCodec, Supplier<U> supplier) {
        return Codec.pair(keyCodec, valueCodec).listOf().<U>xmap(pairs -> {
            var map = supplier.get();
            pairs.forEach(pair -> map.put(pair.getFirst(), pair.getSecond()));

            return map;
        }, map -> map.entrySet().stream().map(a -> Pair.of(a.getKey(), a.getValue())).toList());

    }

    public static <T> Codec<T> reference(Function<Path<String>, @Nullable T> function) {
        return Codec.STRING.flatXmap(s -> DataResult.success(function.apply(Path.stringPath(s))), t -> DataResult.error(() -> "Serialization of modifier reference not supported."));
    }

    public static <T> Codec<T> codecWithReference(Codec<T> base, Function<Path<String>, @Nullable T> function) {
        var reference = reference(function);

        return Codec.withAlternative(base, reference);
    }

    public record Reference<T>(Path<String> reference, Function<Path<String>, T> function) implements Supplier<T> {

        @Override
        public T get() {
            return function.apply(reference);
        }
    }
}
