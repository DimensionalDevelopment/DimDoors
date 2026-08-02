package org.dimdev.dimdoors.block.entity;

import com.mojang.serialization.Codec;

import java.util.function.Function;
import java.util.function.Supplier;

public record CodecRecord<T, V>(String name, Codec<V> codec, Supplier<V> defaultValue, Function<T, V> function) {
    public CodecRecord(String name, Codec<V> codec, V defaultValue, Function<T, V> function) {
        this(name, codec, () -> defaultValue, function);
    }

    public CodecRecord(String name, Codec<V> codec, Function<T, V> function) {
        this(name, codec, (Supplier<V>) null, function);
    }
}
