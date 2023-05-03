package org.dimdev.dimdoors.api.rift.target;

import net.minecraft.item.ItemStack;

public interface ItemTarget extends Target {
	boolean receiveItemStack(ItemStack stack);
}
