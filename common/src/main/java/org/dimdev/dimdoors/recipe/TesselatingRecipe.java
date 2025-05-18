package org.dimdev.dimdoors.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity;
import org.jetbrains.annotations.NotNull;

public interface TesselatingRecipe extends Recipe<CraftingInput> {

    int weavingTime();

    @Override
    default @NotNull RecipeType<TesselatingRecipe> getType() {
        return ModRecipeTypes.TESSELATING.get();
    }

    @Override
    default @NotNull ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.TESSELATING_LOOM.get());
    }
}