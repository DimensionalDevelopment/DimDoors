package org.dimdev.dimdoors.compat.rei.tesselating;

import net.minecraft.world.item.crafting.RecipeHolder;
import org.dimdev.dimdoors.recipe.TesselatingShapelessRecipe;

@SuppressWarnings("ALL")
public class DefaultTesselatingShapelessDisplay extends DefaultTesselatingDisplay<TesselatingShapelessRecipe> {
    public DefaultTesselatingShapelessDisplay(RecipeHolder<TesselatingShapelessRecipe> recipe) {
        super(recipe);
    }

    @Override
    public int getWidth() {
        return getInputEntries().size() > 4 ? 3 : 2;
    }

    @Override
    public int getHeight() {
        return getInputEntries().size() > 4 ? 3 : 2;
    }

    @Override
    public boolean isShapeless() {
        return true;
    }
}
