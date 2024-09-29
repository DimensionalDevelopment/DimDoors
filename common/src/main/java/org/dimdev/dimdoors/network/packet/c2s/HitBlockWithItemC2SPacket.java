package org.dimdev.dimdoors.network.packet.c2s;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.item.ExtendedItem;

public record HitBlockWithItemC2SPacket(InteractionHand hand, BlockPos pos, Direction direction) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<HitBlockWithItemC2SPacket> TYPE = new CustomPacketPayload.Type<>(DimensionalDoors.id("hit_block_with_item"));
	public static final StreamCodec<RegistryFriendlyByteBuf, HitBlockWithItemC2SPacket> STREAM_CODEC = CustomPacketPayload.codec(HitBlockWithItemC2SPacket::write, HitBlockWithItemC2SPacket::new);

	private HitBlockWithItemC2SPacket(FriendlyByteBuf buf) {
		this(buf.readEnum(InteractionHand.class), buf.readBlockPos(), buf.readEnum(Direction.class));
	}

	private void write(FriendlyByteBuf buf) {
		buf.writeEnum(hand);
		buf.writeBlockPos(pos);
		buf.writeEnum(direction);
	}

	public static void handle(HitBlockWithItemC2SPacket packet, NetworkManager.PacketContext context) {
		context.queue(() -> {
			Item item = context.getPlayer().getItemInHand(packet.getHand()).getItem();
			if (item instanceof ExtendedItem) {
				((ExtendedItem) item).onAttackBlock(context.getPlayer().level(), context.getPlayer(), packet.getHand(), packet.getPos(), packet.getDirection());
			}
		});
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

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
