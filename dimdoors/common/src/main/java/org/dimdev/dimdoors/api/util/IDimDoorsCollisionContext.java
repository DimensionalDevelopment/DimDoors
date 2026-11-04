package org.dimdev.dimdoors.api.util;

import net.minecraft.world.item.ItemStack;

public interface IDimDoorsCollisionContext {
    default ItemStack dimensionalDoors$getHeldItem() {
        return ItemStack.EMPTY;
    }
}
