package org.dimdev.dimdoors.rift.targets;

import net.minecraft.item.ItemStack;

public interface ItemTarget extends Target {
	boolean receiveItemStack(ItemStack stack);
}
