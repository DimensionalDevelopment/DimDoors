package org.dimdev.dimdoors.network.packet.s2c;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;

public class RenderBreakBlockS2CPacket implements CustomPacketPayload {
	public static final ResourceLocation ID = DimensionalDoors.id("render_break_block");
	public static final CustomPacketPayload.Type<RenderBreakBlockS2CPacket> TYPE = new CustomPacketPayload.Type<RenderBreakBlockS2CPacket>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, RenderBreakBlockS2CPacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, RenderBreakBlockS2CPacket::getPos,
			ByteBufCodecs.VAR_INT, RenderBreakBlockS2CPacket::getStage,
			RenderBreakBlockS2CPacket::new
	);

	private BlockPos pos;
	private int stage;

	public RenderBreakBlockS2CPacket(BlockPos pos, int stage) {
		this.pos = pos;
		this.stage = stage;
	}

	public RenderBreakBlockS2CPacket(FriendlyByteBuf buf) {
		this(buf.readBlockPos(), buf.readInt());
	}

	public static void apply(RenderBreakBlockS2CPacket packete, NetworkManager.PacketContext context) {
		ClientPacketHandler.getHandler().onRenderBreakBlock(packete);
	}

	public BlockPos getPos() {
		return pos;
	}

	public int getStage() {
		return stage;
	}

	@Override
	public Type<RenderBreakBlockS2CPacket> type() {
		return TYPE;
	}
}
