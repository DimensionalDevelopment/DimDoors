package org.dimdev.dimdoors.network.packet.s2c;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;

import java.util.function.Supplier;

public class PlayerInventorySlotUpdateS2CPacket {
	public static final ResourceLocation ID = DimensionalDoors.id("player_inventory_slot_update");

	private int slot;
	private ItemStack stack;

	@Environment(EnvType.CLIENT)
	public PlayerInventorySlotUpdateS2CPacket() {
		this.stack = ItemStack.EMPTY;
	}

	public PlayerInventorySlotUpdateS2CPacket(int slot, ItemStack stack) {
		this.slot = slot;
		this.stack = stack;
	}

	public PlayerInventorySlotUpdateS2CPacket(FriendlyByteBuf buf) {
		this(buf.readInt(),
		buf.readItem());
	}

	public FriendlyByteBuf write(FriendlyByteBuf buf) {
		buf.writeInt(slot);
		buf.writeItem(stack);
		return buf;
	}

	public void apply(Supplier<NetworkManager.PacketContext> context) {
		ClientPacketHandler.getHandler().onPlayerInventorySlotUpdate(this);
	}

	public int getSlot() {
		return slot;
	}

	public ItemStack getStack() {
		return stack;
	}
}
