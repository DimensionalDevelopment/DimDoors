package org.dimdev.dimdoors.compat.rei.tesselating

import net.minecraft.world.item.crafting.RecipeHolder
import org.dimdev.dimdoors.recipe.TesselatingShapelessRecipe

class DefaultTesselatingShapelessDisplay(recipe: RecipeHolder<TesselatingShapelessRecipe>) : DefaultTesselatingDisplay<TesselatingShapelessRecipe>(recipe) {
    override fun getWidth() = if (inputEntries.size > 4) 3 else 2

    override fun getHeight() = if (inputEntries.size > 4) 3 else 2

    override val isShapeless: Boolean get() = true
}
