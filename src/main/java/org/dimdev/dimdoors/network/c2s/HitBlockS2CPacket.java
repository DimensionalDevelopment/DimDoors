package org.dimdev.dimdoors.network.c2s;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.io.IOException;

// TODO: replace ClientPlayPackListener
public class HitBlockS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final Identifier ID = new Identifier("dimdoors:hit_block");

	private Hand hand;
	private BlockPos pos;
	private Direction direction;

	public HitBlockS2CPacket() {
	}

	@Environment(EnvType.CLIENT)
	public HitBlockS2CPacket(Hand hand, BlockPos pos, Direction direction) {
		this.hand = hand;
		this.pos = pos;
		this.direction = direction;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		hand = buf.readEnumConstant(Hand.class);
		pos = buf.readBlockPos();
		direction = buf.readEnumConstant(Direction.class);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(hand);
		buf.writeBlockPos(pos);
		buf.writeEnumConstant(direction);
	}

	@Override
	public void apply(ClientPlayPacketListener listener) {
		// TODO: write method
	}


	public Hand getHand() {
		return hand;
	}

	public BlockPos getPos() {
		return pos;
	}

	public Direction getDirection() {
		return direction;
	}
}
