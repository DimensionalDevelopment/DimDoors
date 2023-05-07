package org.dimdev.dimdoors.network.packet.s2c;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.SimplePacket;
import org.dimdev.dimdoors.network.client.ClientPacketListener;

import java.io.IOException;

public class RenderBreakBlockS2CPacket implements SimplePacket<ClientPacketListener> {
	public static final ResourceLocation ID = DimensionalDoors.id("render_break_block");

	private BlockPos pos;
	private int stage;

	@Environment(EnvType.CLIENT)
	public RenderBreakBlockS2CPacket() {

	}

	public RenderBreakBlockS2CPacket(BlockPos pos, int stage) {
		this.pos = pos;
		this.stage = stage;
	}

	@Override
	public SimplePacket<ClientPacketListener> read(FriendlyByteBuf buf) throws IOException {
		pos = buf.readBlockPos();
		stage = buf.readInt();
		return this;
	}

	@Override
	public FriendlyByteBuf write(FriendlyByteBuf buf) throws IOException {
		buf.writeBlockPos(pos);
		buf.writeInt(stage);
		return buf;
	}

	@Override
	public void apply(ClientPacketListener listener) {
		listener.onRenderBreakBlock(this);
	}

	@Override
	public ResourceLocation channelId() {
		return ID;
	}

	public BlockPos getPos() {
		return pos;
	}

	public int getStage() {
		return stage;
	}
}
