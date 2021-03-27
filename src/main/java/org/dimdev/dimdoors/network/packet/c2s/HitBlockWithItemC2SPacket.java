package org.dimdev.dimdoors.network.packet.c2s;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import org.dimdev.dimdoors.network.ServerPacketListener;
import org.dimdev.dimdoors.network.SimplePacket;

import java.io.IOException;

public class HitBlockWithItemC2SPacket implements SimplePacket<ServerPacketListener> {
	public static final Identifier ID = new Identifier("dimdoors", "hit_block_with_item");

	private Hand hand;
	private BlockPos pos;
	private Direction direction;

	public HitBlockWithItemC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public HitBlockWithItemC2SPacket(Hand hand, BlockPos pos, Direction direction) {
		this.hand = hand;
		this.pos = pos;
		this.direction = direction;
	}

	@Override
	public SimplePacket<ServerPacketListener> read(PacketByteBuf buf) throws IOException {
		hand = buf.readEnumConstant(Hand.class);
		pos = buf.readBlockPos();
		direction = buf.readEnumConstant(Direction.class);
		return this;
	}

	@Override
	public PacketByteBuf write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(hand);
		buf.writeBlockPos(pos);
		buf.writeEnumConstant(direction);
		return buf;
	}

	@Override
	public void apply(ServerPacketListener listener) {
		listener.onAttackBlock(this);
	}

	@Override
	public Identifier channelId() {
		return ID;
	}

	public BlockPos getPos() {
		return pos;
	}

	public Direction getDirection() {
		return direction;
	}

	public Hand getHand() {
		return hand;
	}
}
