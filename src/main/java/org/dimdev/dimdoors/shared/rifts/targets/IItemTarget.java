package org.dimdev.dimdoors.shared.rifts.targets;

import net.minecraft.item.ItemStack;

public interface IItemTarget extends ITarget {
    public boolean receiveItemStack(ItemStack stack);
}
