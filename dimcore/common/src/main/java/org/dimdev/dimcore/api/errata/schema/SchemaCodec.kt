package org.dimdev.dimcore.api.errata.schema

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.ResourceLocation
import java.util.*
import java.util.function.Function
import java.util.stream.Stream
import kotlin.jvm.optionals.getOrNull

class SchemaCodec<A>(val delegate: Codec<A>, val node: SchemaNode) : Codec<A> {
    override fun <T> encode(input: A, ops: DynamicOps<T>, prefix: T): DataResult<T> = delegate.encode(input, ops, prefix)

    override fun <T> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<A, T>> = delegate.decode(ops, input)

    override fun fieldOf(name: String): SchemaField<A> = SchemaField(delegate.fieldOf(name),
        FieldNode(name, node, Presence.Required)
    )

    override fun optionalFieldOf(name: String): SchemaField<Optional<A>> = SchemaField(delegate.optionalFieldOf(name),
        FieldNode(name, node, Presence.Nullable)
    )

    override fun optionalFieldOf(name: String, defaultValue: A): SchemaField<A> = SchemaField(delegate.optionalFieldOf(name, defaultValue),
        FieldNode(name, node, defaulted(defaultValue))
    )

    override fun lenientOptionalFieldOf(name: String): SchemaField<Optional<A>> = SchemaField(delegate.lenientOptionalFieldOf(name),
        FieldNode(name, node, Presence.Nullable)
    )

    override fun lenientOptionalFieldOf(name: String, defaultValue: A): SchemaField<A> = SchemaField(delegate.lenientOptionalFieldOf(name, defaultValue),
        FieldNode(name, node, defaulted(defaultValue))
    )

    override fun <S> xmap(to: Function<in A, out S>, from: Function<in S, out A>): SchemaCodec<S> = SchemaCodec(delegate.xmap(to, from), node)

    override fun listOf(): SchemaCodec<List<A>> = SchemaCodec(delegate.listOf(), ListNode(node, false))

    fun element(id: ResourceLocation, config: CompoundTag = CompoundTag()): SchemaCodec<A> = SchemaCodec(delegate,
        ElementNode(id, node, config)
    )

    private fun defaulted(defaultValue: A) = Presence.Defaulted { delegate.encodeStart(NbtOps.INSTANCE, defaultValue).result().getOrNull() }

    override fun toString() = "Schema[$delegate]"
}

class SchemaField<A>(val delegate: MapCodec<A>, val field: FieldNode) : MapCodec<A>() {
    override fun <T> keys(ops: DynamicOps<T>): Stream<T> = if (ops === SchemaOps) Stream.of(field as T) else delegate.keys(ops)

    override fun <T> decode(ops: DynamicOps<T>, input: MapLike<T>): DataResult<A> = delegate.decode(ops, input)

    override fun <T> encode(input: A, ops: DynamicOps<T>, prefix: RecordBuilder<T>): RecordBuilder<T> = delegate.encode(input, ops, prefix)

    fun readOnly() = SchemaField(delegate, field.copy(readOnly = true))

    fun label(key: String) = SchemaField(delegate, field.copy(label = key))

    fun overridable() = SchemaField(delegate, field.copy(overridable = true))

    override fun toString() = "SchemaField[$delegate]"
}

fun <A> MapCodec<out A>.schema(id: ResourceLocation): StructNode = StructNode(
    id,
    keys(SchemaOps).map { it as? FieldNode ?: FieldNode(it as String, UntypedNode, Presence.Required) }.toList()
        .groupBy { it.name }.values.map { fields -> fields.firstOrNull { it.node !is UntypedNode } ?: fields.first() })
