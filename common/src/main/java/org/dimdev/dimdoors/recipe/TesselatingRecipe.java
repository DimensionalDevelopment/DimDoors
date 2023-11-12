package org.dimdev.dimdoors.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity;

public interface TesselatingRecipe extends Recipe<TesselatingLoomBlockEntity> {

    boolean matches(TesselatingLoomBlockEntity inv, Level level);

    int weavingTime();
}