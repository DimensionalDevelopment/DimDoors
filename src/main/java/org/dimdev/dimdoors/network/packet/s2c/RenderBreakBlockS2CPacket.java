package org.dimdev.dimdoors.network.packet.s2c;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.dimdev.dimdoors.network.SimplePacket;
import org.dimdev.dimdoors.network.client.ClientPacketListener;

import java.io.IOException;

public class RenderBreakBlockS2CPacket implements SimplePacket<ClientPacketListener> {
	public static final Identifier ID = new Identifier("dimdoors:render_break_block");

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
	public SimplePacket<ClientPacketListener> read(PacketByteBuf buf) throws IOException {
		pos = buf.readBlockPos();
		stage = buf.readInt();
		return this;
	}

	@Override
	public PacketByteBuf write(PacketByteBuf buf) throws IOException {
		buf.writeBlockPos(pos);
		buf.writeInt(stage);
		return buf;
	}

	@Override
	public void apply(ClientPacketListener listener) {
		listener.onRenderBreakBlock(this);
	}

	@Override
	public Identifier channelId() {
		return ID;
	}

	public BlockPos getPos() {
		return pos;
	}

	public int getStage() {
		return stage;
	}
}
