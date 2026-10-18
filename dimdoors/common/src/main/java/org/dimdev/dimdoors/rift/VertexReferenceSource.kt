package org.dimdev.dimdoors.rift

import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import org.dimdev.dimcore.api.errata.reference.ReferenceEntry
import org.dimdev.dimcore.api.errata.reference.ReferenceSource
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.registry.RiftRegistry
import org.dimdev.dimdoors.rift.registry.Vertex
import org.dimdev.dimdoors.util.UUIDExtensions.rift
import kotlin.jvm.optionals.getOrNull

object VertexReferenceSource : ReferenceSource {
    override val id = DimensionalDoors.id("vertex")

    override fun input(player: ServerPlayer, value: Tag): Tag? = decode(value)?.providedLocation?.let { Location.CODEC.encodeStart(NbtOps.INSTANCE, it).result().getOrNull() }

    override fun resolve(player: ServerPlayer, input: Tag): ReferenceEntry? {
        val location = Location.CODEC.parse(NbtOps.INSTANCE, input).result().getOrNull() ?: return null
        if (!RiftRegistry.instance.isRiftAt(location)) return null

        val vertex = location.riftOrPlaceholder().rift()
        val tag = Vertex.CODEC.encodeStart(NbtOps.INSTANCE, vertex).result().getOrNull() ?: return null

        return ReferenceEntry(describe(vertex) ?: return null, tag)
    }

    override fun describe(player: ServerPlayer, value: Tag): Component? = decode(value)?.let(::describe)

    override fun validate(player: ServerPlayer, value: Tag): Boolean = decode(value)?.providedLocation?.let(RiftRegistry.instance::isRiftAt) == true

    private fun decode(value: Tag): Vertex? = Vertex.CODEC.parse(NbtOps.INSTANCE, value).result().getOrNull()

    private fun describe(vertex: Vertex): Component? = vertex.providedLocation?.let { Component.literal("${it.x}, ${it.y}, ${it.z} (${it.worldId.location()})") }
}
