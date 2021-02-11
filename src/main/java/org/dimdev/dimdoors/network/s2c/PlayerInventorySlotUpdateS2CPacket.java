package org.dimdev.dimdoors.network.s2c;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class PlayerInventorySlotUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final Identifier ID = new Identifier("dimdoors:player_inventory_slot_update");

	private int slot;
	private ItemStack stack;

	public PlayerInventorySlotUpdateS2CPacket() {
		this.stack = ItemStack.EMPTY;
	}

	public PlayerInventorySlotUpdateS2CPacket(int slot, ItemStack stack) {
		this.slot = slot;
		this.stack = stack;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		slot = buf.readInt();
		stack = buf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(slot);
		buf.writeItemStack(stack);
	}

	@Override
	public void apply(ClientPlayPacketListener listener) {
		// TODO: write method
	}

	@Environment(EnvType.CLIENT)
	public int getSlot() {
		return slot;
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getStack() {
		return stack;
	}
}
