package org.dimdev.dimdoors.compat.rei;

import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe;

import java.util.Collections;
import java.util.Optional;

public class DefaultTesselatingShapedDisplay extends DefaultTesselatingDisplay<ShapedTesselatingRecipe> {
    public DefaultTesselatingShapedDisplay(ShapedTesselatingRecipe recipe) {
        super(
                EntryIngredients.ofIngredients(recipe.getIngredients()),
                Collections.singletonList(EntryIngredients.of(recipe.getResultItem(BasicDisplay.registryAccess()))),
                Optional.of(recipe),
                recipe.weavingTime()
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public int getWidth() {
        return recipe.get().getWidth();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public int getHeight() {
        return recipe.get().getHeight();
    }
}