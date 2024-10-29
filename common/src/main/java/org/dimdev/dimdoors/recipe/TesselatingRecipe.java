package org.dimdev.dimdoors.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity;

public interface TesselatingRecipe extends Recipe<TesselatingLoomBlockEntity> {

    int weavingTime();

    @Override
    default RecipeType<?> getType() {
        return ModRecipeTypes.TESSELATING.getOrNull();
    }

    @Override
    default ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.TESSELATING_LOOM.get());
    }
}