package org.dimdev.dimcore.api.errata.schema

import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.Tag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

sealed interface RangedNumberNode<T> : SchemaNode {
    val default: T
    val min: T
    val max: T
}

data class IntNode(override val default: Int = 0, override val min: Int = Int.MIN_VALUE, override val max: Int = Int.MAX_VALUE) : RangedNumberNode<Int> {
    override val type get() = SchemaNodeTypes.INT
    val ranged get() = min != Int.MIN_VALUE || max != Int.MAX_VALUE

    override fun defaultTag(schema: ResolvedSchema): Tag = IntTag.valueOf(default)

    companion object {
        val STREAM_CODEC = ByteBufCodecs.VAR_INT.ranged(::IntNode)
    }
}

data class FloatNode(override val default: Float = 0f, override val min: Float = Float.MIN_VALUE, override val max: Float = Float.MAX_VALUE) : RangedNumberNode<Float> {
    override val type get() = SchemaNodeTypes.FLOAT
    val ranged get() = min != Float.MIN_VALUE || max != Float.MAX_VALUE

    override fun defaultTag(schema: ResolvedSchema): Tag = FloatTag.valueOf(default)

    companion object {
        val STREAM_CODEC = ByteBufCodecs.FLOAT.ranged(::FloatNode)
    }
}

data class DoubleNode(override val default: Double = 0.0, override val min: Double = Double.MIN_VALUE, override val max: Double = Double.MAX_VALUE) : RangedNumberNode<Double> {
    override val type get() = SchemaNodeTypes.DOUBLE
    val ranged get() = min != Double.MIN_VALUE || max != Double.MAX_VALUE

    override fun defaultTag(schema: ResolvedSchema): Tag = DoubleTag.valueOf(default)

    companion object {
        val STREAM_CODEC = ByteBufCodecs.DOUBLE.ranged({ a, b, c -> DoubleNode(a, b, c) })
    }
}

private fun <T, V : RangedNumberNode<T>> StreamCodec<in RegistryFriendlyByteBuf, T>.ranged(constructor: (T, T, T) -> V): StreamCodec<RegistryFriendlyByteBuf, V> = StreamCodec.composite<RegistryFriendlyByteBuf, V, T, T, T>(
    this, RangedNumberNode<T>::default,
    this, RangedNumberNode<T>::min,
    this, RangedNumberNode<T>::max,
    constructor
)
