package org.dimdev.dimdoors.network.packet.s2c;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.dimdoors.network.SimplePacket;

import java.io.IOException;

public class PlayerInventorySlotUpdateS2CPacket implements SimplePacket<ClientPacketListener> {
	public static final Identifier ID = new Identifier("dimdoors:player_inventory_slot_update");

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

	@Override
	public SimplePacket<ClientPacketListener> read(PacketByteBuf buf) throws IOException {
		slot = buf.readInt();
		stack = buf.readItemStack();
		return this;
	}

	@Override
	public PacketByteBuf write(PacketByteBuf buf) throws IOException {
		buf.writeInt(slot);
		buf.writeItemStack(stack);
		return buf;
	}

	@Override
	public void apply(ClientPacketListener listener) {
		listener.onPlayerInventorySlotUpdate(this);
	}

	@Override
	public Identifier channelId() {
		return ID;
	}

	public int getSlot() {
		return slot;
	}

	public ItemStack getStack() {
		return stack;
	}
}
