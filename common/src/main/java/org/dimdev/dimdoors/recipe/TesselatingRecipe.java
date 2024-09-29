package org.dimdev.dimdoors.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity;
import org.jetbrains.annotations.NotNull;

public interface TesselatingRecipe extends Recipe<CraftingInput> {

    boolean matches(CraftingInput inv, Level level);

    int weavingTime();

    @Override
    default RecipeType<?> getType() {
        return ModRecipeTypes.TESSELATING.get();
    }

    @Override
    default ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.TESSELATING_LOOM.get());
    }
}