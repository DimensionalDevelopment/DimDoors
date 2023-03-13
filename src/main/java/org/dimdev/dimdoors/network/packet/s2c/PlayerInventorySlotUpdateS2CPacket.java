package org.dimdev.dimdoors.network.packet.s2c;

import java.io.IOException;
import net.fabricmc.api.Dist;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.SimplePacket;
import org.dimdev.dimdoors.network.client.ClientPacketListener;

public class PlayerInventorySlotUpdateS2CPacket implements SimplePacket<ClientPacketListener> {
	public static final ResourceLocation ID = DimensionalDoors.resource("player_inventory_slot_update");

	private int slot;
	private ItemStack stack;

	@Environment(Dist.CLIENT)
	public PlayerInventorySlotUpdateS2CPacket() {
		this.stack = ItemStack.EMPTY;
	}

	public PlayerInventorySlotUpdateS2CPacket(int slot, ItemStack stack) {
		this.slot = slot;
		this.stack = stack;
	}

	@Override
	public SimplePacket<ClientPacketListener> read(FriendlyByteBuf buf) throws IOException {
		slot = buf.readInt();
		stack = buf.readItem();
		return this;
	}

	@Override
	public FriendlyByteBuf write(FriendlyByteBuf buf) throws IOException {
		buf.writeInt(slot);
		buf.writeItem(stack);
		return buf;
	}

	@Override
	public void apply(ClientPacketListener listener) {
		listener.onPlayerInventorySlotUpdate(this);
	}

	@Override
	public ResourceLocation channelId() {
		return ID;
	}

	public int getSlot() {
		return slot;
	}

	public ItemStack getStack() {
		return stack;
	}
}
