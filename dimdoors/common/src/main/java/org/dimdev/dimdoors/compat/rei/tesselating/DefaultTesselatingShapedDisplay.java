package org.dimdev.dimdoors.compat.rei.tesselating;

import net.minecraft.world.item.crafting.RecipeHolder;
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe;

public class DefaultTesselatingShapedDisplay extends DefaultTesselatingDisplay<ShapedTesselatingRecipe> {
    public DefaultTesselatingShapedDisplay(RecipeHolder<ShapedTesselatingRecipe> recipe) {
        super(recipe);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public int getWidth() {
        return recipe.value().getWidth();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public int getHeight() {
        return recipe.value().getHeight();
    }
}