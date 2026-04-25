package org.dimdev.dimdoors.compat.jei;

import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.compat.decay.DecayDisplayData;
import org.dimdev.dimdoors.recipe.TesselatingRecipe;

public class ModRecipeTypes {
    public static final RecipeType<RecipeHolder<TesselatingRecipe>> TESSELATING = RecipeType.createFromVanilla(org.dimdev.dimdoors.recipe.ModRecipeTypes.TESSELATING.get());
    public static final RecipeType<DecayDisplayData> DECAY = RecipeType.create(DimensionalDoors.MOD_ID, "decays_into", DecayDisplayData.class);
}
