package org.dimdev.dimdoors.mixin.client;

import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;

@Mixin(PlaceRecipe.class)
public interface PlaceRecipeMixin<T> extends PlaceRecipe<T> {
    @Shadow
    void addItemToSlot(T ingredient, int slot, int maxAmount, int x, int y);

    /**
     * @author Waterpicker
     * @reason
     */
    @Overwrite
    default void placeRecipe(int width, int height, int outputSlot, RecipeHolder<?> recipeHolder, Iterator<T> ingredients, int maxAmount) {
    int recipeWidth = width;
    int recipeHeight = height;
    Recipe<?> recipe = recipeHolder.value();
    if (recipe instanceof ShapedRecipe shapedRecipe) {
        recipeWidth = shapedRecipe.getWidth();
        recipeHeight = shapedRecipe.getHeight();
    } else if (recipe instanceof ShapedTesselatingRecipe shapedRecipe) {
        recipeWidth = shapedRecipe.getWidth();
        recipeHeight = shapedRecipe.getHeight();
    }

    int slot = 0;

    block0:
    for (int y = 0; y < height; ++y) {
        if (slot == outputSlot) {
        ++slot;
        }

        boolean centerVertically = (float) recipeHeight < (float) height / 2.0F;
        int verticalOffset = Mth.floor((float) height / 2.0F - (float) recipeHeight / 2.0F);
        if (centerVertically && verticalOffset > y) {
        slot += width;
        ++y;
        }

        for (int x = 0; x < width; ++x) {
        if (!ingredients.hasNext()) {
            return;
        }

        boolean centerHorizontally = (float) recipeWidth < (float) width / 2.0F;
        int horizontalOffset = Mth.floor((float) width / 2.0F - (float) recipeWidth / 2.0F);
        int rowEnd = recipeWidth;
        boolean withinRecipe = x < recipeWidth;
        if (centerHorizontally) {
            rowEnd = horizontalOffset + recipeWidth;
            withinRecipe = horizontalOffset <= x && x < rowEnd;
        }

        if (withinRecipe) {
            addItemToSlot(ingredients.next(), slot, maxAmount, x, y);
        } else if (rowEnd == x) {
            slot += width - x;
            continue block0;
        }

        ++slot;
        }
    }
    }
}
