package org.dimdev.dimdoors.mixin.client;

import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Iterator;

@Mixin(PlaceRecipe.class)
public interface PlaceRecipeMixin {
    @ModifyVariable(
            method = "placeRecipe",
            at = @At(value = "STORE", ordinal = 0),
            index = 7
    )
    private static int dimdoors$setTesselatingRecipeWidth(int recipeWidth, int width, int height, int outputSlot, RecipeHolder<?> recipeHolder, Iterator<?> ingredients, int maxAmount) {
        Recipe<?> recipe = recipeHolder.value();
        return recipe instanceof ShapedTesselatingRecipe shapedRecipe ? shapedRecipe.getWidth() : recipeWidth;
    }

    @ModifyVariable(
            method = "placeRecipe",
            at = @At(value = "STORE", ordinal = 1),
            index = 8
    )
    private static int dimdoors$setTesselatingRecipeHeight(int recipeHeight, int width, int height, int outputSlot, RecipeHolder<?> recipeHolder, Iterator<?> ingredients, int maxAmount) {
        Recipe<?> recipe = recipeHolder.value();
        return recipe instanceof ShapedTesselatingRecipe shapedRecipe ? shapedRecipe.getHeight() : recipeHeight;
    }
}
