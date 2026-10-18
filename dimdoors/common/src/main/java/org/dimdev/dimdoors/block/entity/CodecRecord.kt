package org.dimdev.dimdoors.block.entity;

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps

data class CodecRecord<T, V>(val name: String, val codec: Codec<V>, val defaultValue: () -> V, val function: (T) -> V, val setter: (T, V) -> Unit) {
    constructor(name: String, codec: Codec<V>, defaultValue: V, function: (T) -> V, setter: (T, V) -> Unit) : this(name, codec, { defaultValue }, function, setter)

    fun <K> encode(subject: T, ops: DynamicOps<K>): DataResult<K> = codec.encodeStart(ops, function(subject))

    fun <K> parse(ops: DynamicOps<K>, input: K): DataResult<V> = codec.parse(ops, input)
}
