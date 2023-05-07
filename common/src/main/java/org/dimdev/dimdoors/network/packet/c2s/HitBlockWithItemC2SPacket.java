package org.dimdev.dimdoors.network.packet.c2s;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.ServerPacketHandler;

import java.util.function.Supplier;

public class HitBlockWithItemC2SPacket {
	public static final ResourceLocation ID = DimensionalDoors.id("hit_block_with_item");

	private InteractionHand hand;
	private BlockPos pos;
	private Direction direction;

	public HitBlockWithItemC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public HitBlockWithItemC2SPacket(InteractionHand hand, BlockPos pos, Direction direction) {
		this.hand = hand;
		this.pos = pos;
		this.direction = direction;
	}

	public HitBlockWithItemC2SPacket(FriendlyByteBuf buf) {
		this(buf.readEnum(InteractionHand.class), buf.readBlockPos(), buf.readEnum(Direction.class));
	}

	public FriendlyByteBuf write(FriendlyByteBuf buf) {
		buf.writeEnum(hand);
		buf.writeBlockPos(pos);
		buf.writeEnum(direction);
		return buf;
	}

	public void apply(Supplier<NetworkManager.PacketContext> context) {
		ServerPacketHandler.get((ServerPlayer) context.get().getPlayer()).onAttackBlock(this);
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
