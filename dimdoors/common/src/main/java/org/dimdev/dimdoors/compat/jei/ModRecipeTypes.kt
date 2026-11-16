package org.dimdev.dimdoors.compat.jei

import mezz.jei.api.recipe.RecipeType
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.compat.recipeviewer.DecayDisplayData
import org.dimdev.dimdoors.recipe.ModRecipeTypes
import org.dimdev.dimdoors.recipe.tesselating.TesselatingRecipe

object ModRecipeTypes {
    val TESSELATING =
        RecipeType.createFromVanilla<TesselatingRecipe>(ModRecipeTypes.TESSELATING)
    val DECAY = RecipeType.create(DimensionalDoors.MOD_ID, "decays_into", DecayDisplayData::class.java)
}
