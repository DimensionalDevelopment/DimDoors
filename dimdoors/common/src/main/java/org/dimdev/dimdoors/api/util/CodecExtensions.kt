package org.dimdev.dimdoors.api.util

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.Lifecycle
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.mojang.serialization.codecs.UnboundedMapCodec
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import org.dimdev.dimcore.api.ext.cast
import java.util.*

fun <O, T : Any> MapCodec<Optional<T>>.nullableForGetter(getter: (O) -> T?): RecordCodecBuilder<O, Optional<T>> = forGetter { Optional.ofNullable(getter(it)) }

infix fun <K, V> Codec<K>.unboundedMap(valueCodec: Codec<V>): Codec<MutableMap<K, V>> {
    return HashMapCodec(this, valueCodec)
}

fun <K, V> UnboundedMapCodec<K, V>.immutable(): Codec<Map<K, V>> = this.xmap<Map<K, V>>(MutableMap<K, V>::toMap) { it }

fun <T> Codec<T>.imutableList(): Codec<List<T>> = this.listOf().xmap({ it.toList() }, { it })
fun <T> Codec<T>.mutableList(): Codec<MutableList<T>> = this.listOf().xmap({ it.toMutableList() }, { it })

fun <T : Any> Registry<T>.holderCodec(): Codec<Holder<out T>> = this.holderByNameCodec().cast()

fun <T> Codec<T>.toNbt(value: T): Tag {
    return NbtUtil.serialize(value, this)
}

fun Tag?.asNbtCompound(error: String?): CompoundTag = NbtUtil.asNbtCompound(this, error)

private data class HashMapCodec<K, V>(val keyCodec: Codec<K>, val elementCodec: Codec<V>) : Codec<MutableMap<K, V>> {
    override fun <T> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<MutableMap<K, V>, T>> =
        ops.getMapValues(input).setLifecycle(Lifecycle.stable()).flatMap { entries ->
            val read = mutableMapOf<K, V>()
            val failed = mutableListOf<Pair<T, T>>()
            val errors = mutableListOf<String>()
            entries.forEach { pair ->
                val entry = keyCodec.parse(ops, pair.first).apply2stable({ k, v -> Pair.of(k, v) }, elementCodec.parse(ops, pair.second))
                entry.error().ifPresent { errors += it.message(); failed += pair }
                entry.resultOrPartial().ifPresent {
                    if (read.containsKey(it.first)) { errors += "Duplicate entry for key: '${it.first}'"; failed += pair }
                    else read[it.first] = it.second
                }
            }
            if (errors.isEmpty()) DataResult.success(read)
            else DataResult.error({ errors.joinToString("; ") + " missed input: " + ops.createMap(failed.stream()) }, read)
        }.map { Pair.of(it, input) }

    override fun <T> encode(input: MutableMap<K, V>, ops: DynamicOps<T>, prefix: T): DataResult<T> {
        val builder = ops.mapBuilder()
        input.forEach { (k, v) -> builder.add(keyCodec.encodeStart(ops, k), elementCodec.encodeStart(ops, v)) }
        return builder.build(prefix)
    }

    override fun toString() = "HashMapCodec[$keyCodec -> $elementCodec]"
}
