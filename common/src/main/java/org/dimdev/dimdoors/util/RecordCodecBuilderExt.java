package org.dimdev.dimdoors.util;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Function;

public interface RecordCodecBuilderExt<O, F> {
    MapDecoder<F> dimdoors$getDecoder();
    Function<O, MapEncoder<F>> dimdoors$getEncoder();
    Function<O, F> dimdoors$getGetter();

    @SuppressWarnings("unchecked")
    static <O, F> RecordCodecBuilderExt<O, F> of(RecordCodecBuilder<O, F> builder) {
        return (RecordCodecBuilderExt<O, F>) (Object) builder;
    }

    static <K, F> DataResult<F> decode(RecordCodecBuilder<?, F> builder, DynamicOps<K> ops, K data) {
        return ops.getMap(data).flatMap(map -> decode(builder, ops, map));
    }

    @SuppressWarnings("unchecked")
    static <K, F> DataResult<F> decode(RecordCodecBuilder<?, F> builder, DynamicOps<K> ops, MapLike<K> data) {
        return ((RecordCodecBuilderExt<?, F>) (Object) builder).dimdoors$getDecoder().decode(ops, data);
    }

    static <K, F> F get(RecordCodecBuilder<?, F> builder, DynamicOps<K> ops, K data) {
        return decode(builder, ops, data).getOrThrow(IllegalArgumentException::new);
    }

}
