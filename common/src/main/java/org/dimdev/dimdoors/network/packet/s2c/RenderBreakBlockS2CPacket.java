package org.dimdev.dimdoors.network.packet.s2c;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.dimdev.dimdoors.DimensionalDoors;
import org.jetbrains.annotations.NotNull;

public record RenderBreakBlockS2CPacket(BlockPos pos, int stage) implements CustomPacketPayload {
    public static final Identifier ID = DimensionalDoors.id("render_break_block");
    public static final Type<RenderBreakBlockS2CPacket> TYPE = new Type<RenderBreakBlockS2CPacket>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, RenderBreakBlockS2CPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, RenderBreakBlockS2CPacket::pos,
            ByteBufCodecs.VAR_INT, RenderBreakBlockS2CPacket::stage,
            RenderBreakBlockS2CPacket::new
    );

    @Override
    public @NotNull Type<RenderBreakBlockS2CPacket> type() {
        return TYPE;
    }
}
