package org.dimdev.dimdoors.network.packet.c2s

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.InteractionHand
import org.dimdev.dimdoors.api.util.type

data class HitBlockWithItemC2SPacket(val hand: InteractionHand, val pos: BlockPos, val direction: Direction) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<HitBlockWithItemC2SPacket> = TYPE

    companion object {
        val STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.idMapper(InteractionHand.entries::get, InteractionHand::ordinal), HitBlockWithItemC2SPacket::hand,
                BlockPos.STREAM_CODEC, HitBlockWithItemC2SPacket::pos,
                Direction.STREAM_CODEC, HitBlockWithItemC2SPacket::direction,
            ::HitBlockWithItemC2SPacket
        ).cast<RegistryFriendlyByteBuf>()

        val TYPE = "hit_block_with_item".type<HitBlockWithItemC2SPacket>()
    }
}
