package org.dimdev.dimdoors.block;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IDimDoorsCollisionContext {
    default ItemStack dimensionalDoors$getHeldItem() {
        return ItemStack.EMPTY;
    }

    default ItemStack dimensionalDoors$getOffhandItem() {
        return ItemStack.EMPTY;
    }

    default boolean dimensionalDoors$isHolding(TagKey<Item> tag) {
        return dimensionalDoors$getHeldItem().is(tag) || dimensionalDoors$getOffhandItem().is(tag);
    }
}
