package org.dimdev.dimdoors.network.s2c

import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import org.dimdev.dimdoors.pockets.addon.PocketAddon
import org.dimdev.dimdoors.util.ext.nullable
import org.dimdev.dimdoors.util.ext.streamCodec
import org.dimdev.dimdoors.util.ext.type


data class SyncPocketAddonsS2CPacket(
    val world: ResourceKey<Level>?,
    val addons: List<PocketAddon>
) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> = TYPE

    companion object {
        val TYPE = "sync_pocket_addons".type<SyncPocketAddonsS2CPacket>()
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SyncPocketAddonsS2CPacket> = StreamCodec.composite(
            Registries.DIMENSION.streamCodec.nullable(),
            SyncPocketAddonsS2CPacket::world,
            PocketAddon.LIST_STREAM_CODEC,
            SyncPocketAddonsS2CPacket::addons, ::SyncPocketAddonsS2CPacket
        )
    }
}