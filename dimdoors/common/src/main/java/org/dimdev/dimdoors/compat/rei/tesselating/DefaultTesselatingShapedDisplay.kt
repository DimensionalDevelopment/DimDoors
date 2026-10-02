package org.dimdev.dimdoors.compat.rei.tesselating

import net.minecraft.world.item.crafting.RecipeHolder
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe

class DefaultTesselatingShapedDisplay(recipe: RecipeHolder<ShapedTesselatingRecipe>) : DefaultTesselatingDisplay<ShapedTesselatingRecipe>(recipe) {
    override fun getWidth(): Int {
        return recipe.value().width
    }

    override fun getHeight(): Int {
        return recipe.value().height
    }
}