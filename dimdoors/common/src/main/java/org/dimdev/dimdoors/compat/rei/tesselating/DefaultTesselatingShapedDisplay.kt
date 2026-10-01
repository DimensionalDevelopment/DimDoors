package org.dimdev.dimdoors.compat.rei.tesselating

import net.minecraft.world.item.crafting.RecipeHolder
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe

class DefaultTesselatingShapedDisplay(recipe: RecipeHolder<ShapedTesselatingRecipe>) : DefaultTesselatingDisplay<ShapedTesselatingRecipe>(recipe) {
    override fun getWidth(): Int {
        return optionalRecipe.value().width
    }

    override fun getHeight(): Int {
        return optionalRecipe.value().height
    }
}