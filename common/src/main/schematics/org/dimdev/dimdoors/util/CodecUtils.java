package org.dimdev.dimdoors.util;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
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

    public static <T> Codec<T> reference(Function<String, @Nullable T> function) {
        return Codec.STRING.flatXmap(reference -> {
            var t = function.apply(reference);
            return t != null ? DataResult.success(t) : DataResult.error(() -> reference + " doens't correspond to anything.");
        }, t -> DataResult.error(() -> "Serialization of modifier reference not supported."));
    }

    public static <T> Codec<T> codecWithReference(Codec<T> base, Function<String, @Nullable T> function) {
        var reference = reference(function);

        return Codec.either(reference, base).xmap(Either::orThrow, Either::right);
    }


}
