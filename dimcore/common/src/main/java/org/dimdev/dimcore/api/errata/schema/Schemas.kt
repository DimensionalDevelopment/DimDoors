package org.dimdev.dimcore.api.errata.schema

import com.mojang.datafixers.kinds.App
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimcore.api.errata.reference.ReferenceSource
import org.dimdev.dimcore.api.errata.reference.ReferenceSources

object Schemas {
    @JvmField val BOOL = SchemaCodec(Codec.BOOL, BoolNode())
    @JvmField val INT = SchemaCodec(Codec.INT, IntNode())
    @JvmField val FLOAT = SchemaCodec(Codec.FLOAT, FloatNode())
    @JvmField val DOUBLE = SchemaCodec(Codec.DOUBLE, DoubleNode())
    @JvmField val STRING = SchemaCodec(Codec.STRING, StringNode())

    fun int(min: Int, max: Int, default: Int = min): SchemaCodec<Int> = SchemaCodec(Codec.INT.xmap({ it.coerceIn(min, max) }, { it }),
        IntNode(default, min, max)
    )

    fun float(min: Float, max: Float, default: Float = min): SchemaCodec<Float> = SchemaCodec(Codec.FLOAT.xmap({ it.coerceIn(min, max) }, { it }),
        FloatNode(default, min, max)
    )

    fun <O> record(id: ResourceLocation, builder: (RecordCodecBuilder.Instance<O>) -> App<RecordCodecBuilder.Mu<O>, O>): SchemaCodec<O> {
        val codec = RecordCodecBuilder.mapCodec<O> { builder(it) }
        return SchemaCodec(codec.codec(), codec.schema(id))
    }

    fun <T> registryKey(key: ResourceKey<out Registry<T>>): SchemaCodec<ResourceKey<T>> = SchemaCodec(ResourceKey.codec(key),
        ReferenceNode(ReferenceSources.registry(key).id)
    )

    fun <T> holder(key: ResourceKey<out Registry<T>>, codec: Codec<Holder<T>>): SchemaCodec<Holder<T>> = SchemaCodec(codec,
        ReferenceNode(ReferenceSources.registry(key).id)
    )

    fun <A> reference(source: ReferenceSource, codec: Codec<A>): SchemaCodec<A> = SchemaCodec(codec,
        ReferenceNode(ReferenceSources.register(source).id)
    )

    fun <T, B, A> dispatch(registry: Registry<T>, codec: Codec<A>, default: ResourceLocation? = null, typeKey: String = "type", variant: (T) -> MapCodec<out B>): SchemaCodec<A> {
        DispatchSchemas.register(registry, variant)
        return SchemaCodec(codec, DispatchNode(registry.key().location(), typeKey, default))
    }
}

fun <A> SchemaCodec<A>.setOf(): SchemaCodec<MutableSet<A>> = SchemaCodec(delegate.listOf().xmap({ it.toMutableSet() }, { it.toList() }),
    ListNode(node, true)
)

object ElementIds {
    @JvmField val COLOR: ResourceLocation = ResourceLocation.fromNamespaceAndPath("dimcore", "color")
}
