package org.dimdev.dimdoors.network.packet.s2c;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;

public class PlayerInventorySlotUpdateS2CPacket implements CustomPacketPayload {
	public static final ResourceLocation ID = DimensionalDoors.id("player_inventory_slot_update");
	public static final Type<PlayerInventorySlotUpdateS2CPacket> TYPE = new Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, PlayerInventorySlotUpdateS2CPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, PlayerInventorySlotUpdateS2CPacket::getSlot,
			ItemStack.STREAM_CODEC, PlayerInventorySlotUpdateS2CPacket::getStack,
			PlayerInventorySlotUpdateS2CPacket::new
	);

	private final int slot;
	private final ItemStack stack;

	public PlayerInventorySlotUpdateS2CPacket(int slot, ItemStack stack) {
		this.slot = slot;
		this.stack = stack;
	}

	public static void apply(PlayerInventorySlotUpdateS2CPacket packet, NetworkManager.PacketContext context) {
		ClientPacketHandler.getHandler().onPlayerInventorySlotUpdate(packet);
	}

	public int getSlot() {
		return slot;
	}

	public ItemStack getStack() {
		return stack;
	}

	@Override
	public Type<PlayerInventorySlotUpdateS2CPacket> type() {
		return TYPE;
	}
}
