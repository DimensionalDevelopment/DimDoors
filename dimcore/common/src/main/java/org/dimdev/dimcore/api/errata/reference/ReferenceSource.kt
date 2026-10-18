package org.dimdev.dimcore.api.errata.reference

import net.minecraft.core.Registry
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import org.dimdev.dimcore.api.ext.nullable
import kotlin.jvm.optionals.getOrNull

data class ReferenceEntry(val label: Component?, val value: Tag) {
    companion object {
        @JvmField val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ReferenceEntry> = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC.nullable(), ReferenceEntry::label,
            ByteBufCodecs.TAG, ReferenceEntry::value,
            ::ReferenceEntry
        )
    }
}

interface ReferenceSource {
    val id: ResourceLocation

    fun options(player: ServerPlayer): List<ReferenceEntry> = emptyList()

    fun input(player: ServerPlayer, value: Tag): Tag? = null

    fun resolve(player: ServerPlayer, input: Tag): ReferenceEntry? = null

    fun describe(player: ServerPlayer, value: Tag): Component?

    fun validate(player: ServerPlayer, value: Tag): Boolean
}

object ReferenceSources {
    private val sources = mutableMapOf<ResourceLocation, ReferenceSource>()

    fun <S : ReferenceSource> register(source: S): S = source.also { sources[it.id] = it }

    operator fun get(id: ResourceLocation): ReferenceSource? = sources[id]

    fun <T> registry(key: ResourceKey<out Registry<T>>): ReferenceSource = sources.getOrPut(RegistrySource.id(key)) { RegistrySource(key) }
}

class RegistrySource<T>(private val registry: ResourceKey<out Registry<T>>) : ReferenceSource {
    override val id = id(registry)

    override fun options(player: ServerPlayer): List<ReferenceEntry> = lookup(player)?.keySet()?.sorted()?.map { ReferenceEntry(null, StringTag.valueOf(it.toString())) } ?: emptyList()

    override fun describe(player: ServerPlayer, value: Tag): Component = Component.literal(value.asString)

    override fun validate(player: ServerPlayer, value: Tag): Boolean = ResourceLocation.tryParse(value.asString)?.let { lookup(player)?.containsKey(it) } == true

    private fun lookup(player: ServerPlayer): Registry<T>? = player.server.registryAccess().registry(registry).getOrNull()

    companion object {
        fun <T> id(key: ResourceKey<out Registry<T>>): ResourceLocation = ResourceLocation.fromNamespaceAndPath("dimcore", "registry/${key.location().namespace}/${key.location().path}")
    }
}
