package org.dimdev.dimdoors.network.packet.s2c;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.dimdev.dimdoors.DimensionalDoors;

public record RenderBreakBlockS2CPacket(BlockPos pos, int stage) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<RenderBreakBlockS2CPacket> TYPE = new CustomPacketPayload.Type<>(DimensionalDoors.id("render_break_block"));
	public static final StreamCodec<RegistryFriendlyByteBuf, RenderBreakBlockS2CPacket> STREAM_CODEC = CustomPacketPayload.codec(RenderBreakBlockS2CPacket::write, RenderBreakBlockS2CPacket::new);

	public RenderBreakBlockS2CPacket(RegistryFriendlyByteBuf buf) {
		this(buf.readBlockPos(), buf.readInt());
	}

	public void write(RegistryFriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(stage);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
