package org.dimdev.dimdoors.compat.jei.tesselating;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.dimdev.dimdoors.recipe.TesselatingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface ITesselatingCategoryExtension<T extends TesselatingRecipe> extends IRecipeCategoryExtension<RecipeHolder<T>> {
    default void setRecipe(RecipeHolder<T> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        this.setRecipe(builder, craftingGridHelper, focuses);
    }

    default void onDisplayedIngredientsUpdate(RecipeHolder<T> recipeHolder, List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
    }

    /** @deprecated */
    @Deprecated(
            since = "19.4.1",
            forRemoval = true
    )
    default Optional<Identifier> getRegistryName(RecipeHolder<T> recipeHolder) {
        return Optional.ofNullable(this.getRegistryName()).or(() -> {
            return Optional.of(recipeHolder.id());
        });
    }

    default int getWidth(RecipeHolder<T> recipeHolder) {
        return this.getWidth();
    }

    default int getHeight(RecipeHolder<T> recipeHolder) {
        return this.getHeight();
    }

    /** @deprecated */
    @Deprecated(
            since = "16.0.0",
            forRemoval = true
    )
    default void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {

    }

    /** @deprecated */
    @Deprecated(
            since = "16.0.0",
            forRemoval = true
    )
    default @Nullable Identifier getRegistryName() {
        return null;
    }

    /** @deprecated */
    @Deprecated(
            since = "16.0.0",
            forRemoval = true
    )
    default int getWidth() {
        return 0;
    }

    /** @deprecated */
    @Deprecated(
            since = "16.0.0",
            forRemoval = true
    )
    default int getHeight() {
        return 0;
    }
}