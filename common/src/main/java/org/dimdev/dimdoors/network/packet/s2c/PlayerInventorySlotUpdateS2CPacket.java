package org.dimdev.dimdoors.network.packet.s2c;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;

import java.util.function.Supplier;

public class PlayerInventorySlotUpdateS2CPacket implements CustomPacketPayload {
	public static CustomPacketPayload.Type<PlayerInventorySlotUpdateS2CPacket> TYPE = new CustomPacketPayload.Type<>(DimensionalDoors.id("player_inventory_slot_update"));

	public static final String ID = "player_inventory_slot_update";

	private int slot;
	private ItemStack stack;

	@Environment(EnvType.CLIENT)
	public PlayerInventorySlotUpdateS2CPacket() {
		super();
		this.stack = ItemStack.EMPTY;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@Override
	public void handle(NetworkManager.PacketContext context) {
		ClientPacketHandler.getHandler().onPlayerInventorySlotUpdate(this);
	}

	public PlayerInventorySlotUpdateS2CPacket(int slot, ItemStack stack) {
		this.slot = slot;
		this.stack = stack;
	}

	public PlayerInventorySlotUpdateS2CPacket(RegistryFriendlyByteBuf buf) {
		this(buf.readInt(), ItemStack.STREAM_CODEC.decode(buf));
	}

	public void write(RegistryFriendlyByteBuf buf) {
		buf.writeInt(slot);
		ItemStack.STREAM_CODEC.buf.writeItem(stack);
	}

	public int getSlot() {
		return slot;
	}

	public ItemStack getStack() {
		return stack;
	}
}
