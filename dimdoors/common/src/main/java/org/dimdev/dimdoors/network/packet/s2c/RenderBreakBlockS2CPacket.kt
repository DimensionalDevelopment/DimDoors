package org.dimdev.dimdoors.network.packet.s2c

import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import org.dimdev.dimdoors.network.packet.type

data class RenderBreakBlockS2CPacket(val pos: BlockPos, val stage: Int) : CustomPacketPayload {
    override fun type() = TYPE

    companion object {
        val TYPE = "render_break_block".type<RenderBreakBlockS2CPacket>()
        val STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, RenderBreakBlockS2CPacket::pos,
                ByteBufCodecs.VAR_INT, RenderBreakBlockS2CPacket::stage,
                ::RenderBreakBlockS2CPacket
            ).cast<RegistryFriendlyByteBuf>()
    }
}
