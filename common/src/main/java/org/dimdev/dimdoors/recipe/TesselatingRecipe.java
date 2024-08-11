package org.dimdev.dimdoors.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity;
import org.jetbrains.annotations.NotNull;

public interface TesselatingRecipe extends Recipe<TesselatingLoomBlockEntity> {

    boolean matches(TesselatingLoomBlockEntity inv, Level level);

    int weavingTime();

    @Override
    default @NotNull NonNullList<ItemStack> getRemainingItems(TesselatingLoomBlockEntity input) {
        int width = 3;
        int height = 3;
        var list = NonNullList.withSize(width * height, ItemStack.EMPTY);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                var item = input.getItem(x + y * width).getItem();
                if(item.hasCraftingRemainingItem()) {
                    list.set(x + y * width, new ItemStack(item.getCraftingRemainingItem()));
                }
            }
        }

        return list;
    }

    @Override
    default ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.TESSELATING_LOOM.get());
    }
}