package org.dimdev.dimdoors.block.entity;

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimcore.api.ext.set
import org.dimdev.dimdoors.DimensionalDoors

data class CodecRecord<T, V>(val name: String, val codec: Codec<V>, val defaultValue: () -> V, val function: (T) -> V, val setter: (T, V) -> Unit) {
    constructor(name: String, codec: Codec<V>, defaultValue: V, function: (T) -> V, setter: (T, V) -> Unit) : this(name, codec, { defaultValue }, function, setter)

    fun <K> encode(subject: T, ops: DynamicOps<K>): DataResult<K> = codec.encodeStart(ops, function(subject))

    fun <K> parse(ops: DynamicOps<K>, input: K): DataResult<V> = codec.parse(ops, input)
}

data class Deserialize<K>(val data: K, val ops: DynamicOps<K>) {
    fun <V, O> get(builder: CodecRecord<V, O>): O {
        val field = ops.get(data, builder.name).result().orElse(null) ?: return builder.defaultValue()

        return builder.parse(ops, field)
            .ifError { DimensionalDoors.LOGGER.error("Failed to decode rift field '{}': {}", builder.name, it.message()) }
            .result().orElseGet(builder.defaultValue)
    }
    companion object {
        fun <V> create(ops: DynamicOps<V>, provider: HolderLookup.Provider? = null, value: V): Deserialize<V> = Deserialize(value, if(provider != null) provider.createSerializationContext(ops) else ops)
        fun nbt(provider: HolderLookup.Provider? = null, value: CompoundTag): Deserialize<Tag> = create(NbtOps.INSTANCE, provider, value)
        fun json(provider: HolderLookup.Provider? = null, value: JsonObject): Deserialize<JsonElement> = create(JsonOps.INSTANCE, provider, value)
    }
}

data class Serialize<K, T>(
    val data: K,
    val ops: DynamicOps<K>,
    val subject: T,
    val consumer: (String, K, K) -> Unit
) {
    fun <O> put(builder: CodecRecord<in T, O>) {
        val value = builder.encode(subject, ops).getOrThrow()
        consumer.invoke(builder.name, data, value)
    }

    companion object {
        fun <V, S> create(ops: DynamicOps<V>, provider: HolderLookup.Provider? = null, value: V, subject: S, consumer: (String, V, V) -> Unit): Serialize<V, S> = Serialize(value, if(provider != null) provider.createSerializationContext(ops) else ops, subject, consumer)
        fun <S> nbt(provider: HolderLookup.Provider? = null, value: CompoundTag, subject: S, consumer: (String, Tag, Tag) -> Unit = { name, value, data -> value.cast<CompoundTag>()[name] = data }): Serialize<Tag, S> = create(NbtOps.INSTANCE, provider, value, subject, consumer)
        fun <S> json(provider: HolderLookup.Provider? = null, value: JsonObject, subject: S, consumer: (String, JsonElement, JsonElement) -> Unit = { name, value, data -> data.cast<JsonObject>().add(name, value) }): Serialize<JsonElement, S> = create(JsonOps.INSTANCE, provider, value, subject, consumer)
    }
}