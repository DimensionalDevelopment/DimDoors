package org.dimdev.dimdoors.block.entity;

import com.mojang.serialization.Codec

data class CodecRecord<T, V>(val name: String, val codec: Codec<V>, val defaultValue: () -> V, val function: (T) -> V) {
    constructor(name: String, codec: Codec<V>, defaultValue: V, function: (T) -> V) : this(name, codec, { defaultValue }, function)
}
