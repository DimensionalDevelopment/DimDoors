package org.dimdev.dimdoors.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity;

public interface TesselatingRecipe extends Recipe<TesselatingLoomBlockEntity> {

    boolean matches(TesselatingLoomBlockEntity inv, Level level);

    int weavingTime();

    @Override
    default ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.TESSELATING_LOOM.get());
    }
}