package org.dimdev.dimdoors.network.packet.c2s;

import java.io.IOException;
import net.fabricmc.api.Dist;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.ServerPacketListener;
import org.dimdev.dimdoors.network.SimplePacket;

public class HitBlockWithItemC2SPacket implements SimplePacket<ServerPacketListener> {
	public static final ResourceLocation ID = DimensionalDoors.resource("hit_block_with_item");

	private InteractionHand hand;
	private BlockPos pos;
	private Direction direction;

	public HitBlockWithItemC2SPacket() {
	}

	@Environment(Dist.CLIENT)
	public HitBlockWithItemC2SPacket(InteractionHand hand, BlockPos pos, Direction direction) {
		this.hand = hand;
		this.pos = pos;
		this.direction = direction;
	}

	@Override
	public SimplePacket<ServerPacketListener> read(FriendlyByteBuf buf) throws IOException {
		hand = buf.readEnum(InteractionHand.class);
		pos = buf.readBlockPos();
		direction = buf.readEnum(Direction.class);
		return this;
	}

	@Override
	public FriendlyByteBuf write(FriendlyByteBuf buf) throws IOException {
		buf.writeEnum(hand);
		buf.writeBlockPos(pos);
		buf.writeEnum(direction);
		return buf;
	}

	@Override
	public void apply(ServerPacketListener listener) {
		listener.onAttackBlock(this);
	}

	@Override
	public ResourceLocation channelId() {
		return ID;
	}

	public BlockPos getPos() {
		return pos;
	}

	public Direction getDirection() {
		return direction;
	}

	public InteractionHand getHand() {
		return hand;
	}
}
