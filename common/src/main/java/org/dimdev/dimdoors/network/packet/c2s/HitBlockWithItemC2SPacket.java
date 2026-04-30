package org.dimdev.dimdoors.network.packet.c2s;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import org.dimdev.dimdoors.DimensionalDoors;

public record HitBlockWithItemC2SPacket(InteractionHand hand, BlockPos pos, Direction direction) implements CustomPacketPayload {
    public static final ResourceLocation ID = DimensionalDoors.id("hit_block_with_item");
    public static final StreamCodec<FriendlyByteBuf, HitBlockWithItemC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(value -> InteractionHand.values()[value], Enum::ordinal), HitBlockWithItemC2SPacket::hand,
            BlockPos.STREAM_CODEC, HitBlockWithItemC2SPacket::pos,
            Direction.STREAM_CODEC, HitBlockWithItemC2SPacket::direction,
            HitBlockWithItemC2SPacket::new);
    public static final Type<HitBlockWithItemC2SPacket> TYPE = new Type<>(ID);


    public FriendlyByteBuf write(FriendlyByteBuf buf) {
        buf.writeEnum(hand);
        buf.writeBlockPos(pos);
        buf.writeEnum(direction);
        return buf;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


}
