package org.dimdev.dimdoors.network.packet.s2c

import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.dimdev.dimdoors.api.util.streamCodec
import org.dimdev.dimdoors.api.util.type
import org.dimdev.dimdoors.util.StreamCodecUtils
import org.dimdev.dimdoors.api.util.nullable
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon


data class SyncPocketAddonsS2CPacket(
    val world: ResourceKey<Level>?,
    val box: BoundingBox,
    val addons: List<PocketAddon>
) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> = TYPE

    companion object {
        val TYPE = "sync_pocket_addons".type<SyncPocketAddonsS2CPacket>()
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SyncPocketAddonsS2CPacket> = StreamCodec.composite(
            Registries.DIMENSION.streamCodec.nullable(),
            SyncPocketAddonsS2CPacket::world,
            StreamCodecUtils.BOUNDING_BOX,
            SyncPocketAddonsS2CPacket::box,
            PocketAddon.LIST_STREAM_CODEC,
            SyncPocketAddonsS2CPacket::addons, ::SyncPocketAddonsS2CPacket
        )
    }
}