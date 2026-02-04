package org.dimdev.dimdoors.network.packet.c2s;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.ServerPacketHandler;

public class HitBlockWithItemC2SPacket implements CustomPacketPayload {
	public static final ResourceLocation ID = DimensionalDoors.id("hit_block_with_item");
	public static final StreamCodec<FriendlyByteBuf, HitBlockWithItemC2SPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.idMapper(value -> InteractionHand.values()[value], Enum::ordinal), HitBlockWithItemC2SPacket::getHand,
			BlockPos.STREAM_CODEC, HitBlockWithItemC2SPacket::getPos,
			Direction.STREAM_CODEC, HitBlockWithItemC2SPacket::getDirection,
			HitBlockWithItemC2SPacket::new);
	public static final CustomPacketPayload.Type<HitBlockWithItemC2SPacket> TYPE = new CustomPacketPayload.Type<>(ID);


	private InteractionHand hand;
	private BlockPos pos;
	private Direction direction;

	public HitBlockWithItemC2SPacket() {
	}

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

	public static void apply(HitBlockWithItemC2SPacket packet, NetworkManager.PacketContext context) {
		ServerPacketHandler.get((ServerPlayer) context.getPlayer()).onAttackBlock(packet);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
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
