package org.dimdev.dimdoors.recipe;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface TesselatingContainer extends CraftingContainer {
    int getWidth();

    int getHeight();

    List<ItemStack> getItems();
}
