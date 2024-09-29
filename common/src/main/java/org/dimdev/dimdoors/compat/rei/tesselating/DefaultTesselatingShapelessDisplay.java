package org.dimdev.dimdoors.compat.rei.tesselating;

import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.dimdev.dimdoors.compat.rei.tesselating.DefaultTesselatingDisplay;
import org.dimdev.dimdoors.recipe.TesselatingShapelessRecipe;

import java.util.Collections;
import java.util.Optional;

@SuppressWarnings("ALL")
public class DefaultTesselatingShapelessDisplay extends DefaultTesselatingDisplay<TesselatingShapelessRecipe> {
    public DefaultTesselatingShapelessDisplay(RecipeHolder<TesselatingShapelessRecipe> recipe) {
        super(
                EntryIngredients.ofIngredients(recipe.value().getIngredients()),
                Collections.singletonList(EntryIngredients.of(recipe.value().getResultItem(BasicDisplay.registryAccess()))),
                Optional.of(recipe),
                recipe.value().weavingTime()
        );
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
    public int getInputWidth() {
        return Math.min(getInputEntries().size(), 3);
    }

    @Override
    public int getInputWidth(int craftingWidth, int craftingHeight) {
        return craftingWidth * craftingHeight <= getInputEntries().size() ? craftingWidth : Math.min(getInputEntries().size(), 3);
    }

    @Override
    public int getInputHeight() {
        return (int) Math.ceil(getInputEntries().size() / (double) getInputWidth());
    }

    @Override
    public int getInputHeight(int craftingWidth, int craftingHeight) {
        return (int) Math.ceil(getInputEntries().size() / (double) getInputWidth(craftingWidth, craftingHeight));
    }

    @Override
    public boolean isShapeless() {
        return true;
    }
}
