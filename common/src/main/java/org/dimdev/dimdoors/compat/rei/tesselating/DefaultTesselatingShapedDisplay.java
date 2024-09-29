package org.dimdev.dimdoors.compat.rei.tesselating;

import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.dimdev.dimdoors.compat.rei.tesselating.DefaultTesselatingDisplay;
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe;

import java.util.Collections;
import java.util.Optional;

public class DefaultTesselatingShapedDisplay extends DefaultTesselatingDisplay<ShapedTesselatingRecipe> {
    public DefaultTesselatingShapedDisplay(RecipeHolder<ShapedTesselatingRecipe> recipe) {
        super(
                EntryIngredients.ofIngredients(recipe.value().getIngredients()),
                Collections.singletonList(EntryIngredients.of(recipe.value().getResultItem(BasicDisplay.registryAccess()))),
                Optional.of(recipe),
                recipe.value().weavingTime()
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public int getWidth() {
        return recipe.get().value().getWidth();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public int getHeight() {
        return recipe.get().value().getHeight();
    }
}