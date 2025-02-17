package org.dimdev.dimdoors.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

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

    public static <T> Codec<T> reference(Function<String, T> function) {
        return Codec.STRING.flatXmap(reference -> {
            var t = function.apply(reference);
            return t != null ? DataResult.success(t) : DataResult.error(() -> reference + " doens't correspond to anything.");
        }, t -> DataResult.error(() -> "Can't serialize a reference."));
    };
}
