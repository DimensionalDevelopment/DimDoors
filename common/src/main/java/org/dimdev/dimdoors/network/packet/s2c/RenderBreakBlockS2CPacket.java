package org.dimdev.dimdoors.network.packet.s2c;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;

import java.util.function.Supplier;

public class RenderBreakBlockS2CPacket {
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

	public RenderBreakBlockS2CPacket(FriendlyByteBuf buf) {
		this(buf.readBlockPos(), buf.readInt());
	}

	public FriendlyByteBuf write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(stage);
		return buf;
	}

	public void apply(Supplier<NetworkManager.PacketContext> context) {
		ClientPacketHandler.getHandler().onRenderBreakBlock(this);
	}

	public BlockPos getPos() {
		return pos;
	}

	public int getStage() {
		return stage;
	}
}
