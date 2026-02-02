package org.dimdev.dimdoors.compat.jei;

import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.dimdev.dimdoors.recipe.TesselatingRecipe;

public class ModRecipeTypes {
    public static final RecipeType<RecipeHolder<TesselatingRecipe>> TESSELATING = RecipeType.createFromVanilla(org.dimdev.dimdoors.recipe.ModRecipeTypes.TESSELATING.get());
}
