package org.dimdev.dimdoors.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

@Mixin(CraftingInventory.class)
public interface CraftingInventoryAccessor {
    @Mutable
    @Accessor("stacks")
    void setInventory(DefaultedList<ItemStack> inventory);
}
