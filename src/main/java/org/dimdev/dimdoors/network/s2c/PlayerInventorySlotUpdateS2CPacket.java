package org.dimdev.dimdoors.network.s2c;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.network.ClientPacketHandler;
import org.dimdev.dimdoors.network.SimplePacket;

import java.io.IOException;

public class PlayerInventorySlotUpdateS2CPacket implements SimplePacket<ClientPacketHandler> {
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
	public SimplePacket<ClientPacketHandler> read(PacketByteBuf buf) throws IOException {
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
	public void apply(ClientPacketHandler listener) {
		listener.onPlayerInventorySlotUpdate(slot, stack);
	}

	@Override
	public Identifier channelId() {
		return ID;
	}
}
