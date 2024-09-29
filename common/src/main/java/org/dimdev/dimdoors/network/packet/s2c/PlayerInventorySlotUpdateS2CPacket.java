package org.dimdev.dimdoors.network.packet.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.DimensionalDoors;

public record PlayerInventorySlotUpdateS2CPacket(int slot, ItemStack stack) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PlayerInventorySlotUpdateS2CPacket> TYPE = new CustomPacketPayload.Type<PlayerInventorySlotUpdateS2CPacket>(DimensionalDoors.id("player_inventory_slot_update"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PlayerInventorySlotUpdateS2CPacket> STREAM_CODEC = CustomPacketPayload.codec(PlayerInventorySlotUpdateS2CPacket::write, PlayerInventorySlotUpdateS2CPacket::new);

	private PlayerInventorySlotUpdateS2CPacket(RegistryFriendlyByteBuf buf) {
		this(buf.readInt(),
		ItemStack.STREAM_CODEC.decode(buf));
	}

	private RegistryFriendlyByteBuf write(RegistryFriendlyByteBuf buf) {
		buf.writeInt(slot);
		ItemStack.STREAM_CODEC.encode(buf, stack);
		return buf;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
