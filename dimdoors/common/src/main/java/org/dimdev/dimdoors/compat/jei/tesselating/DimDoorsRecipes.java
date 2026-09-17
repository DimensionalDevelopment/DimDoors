package org.dimdev.dimdoors.compat.jei.tesselating;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.library.plugins.vanilla.crafting.CategoryRecipeValidator;
import mezz.jei.library.util.RecipeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.dimdev.dimdoors.recipe.ModRecipeTypes;
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe;
import org.dimdev.dimdoors.recipe.TesselatingRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class DimDoorsRecipes {
    private final RecipeManager recipeManager;
    private final IIngredientManager ingredientManager;

    public DimDoorsRecipes(IIngredientManager ingredientManager) {
    Minecraft minecraft = Minecraft.getInstance();
    ClientLevel world = minecraft.level;
    this.recipeManager = world != null ? world.getRecipeManager() : null;
    this.ingredientManager = ingredientManager;
    }

    public Map<Boolean, List<RecipeHolder<TesselatingRecipe>>> getTesselating(IRecipeCategory<RecipeHolder<TesselatingRecipe>> craftingCategory) {
    if (recipeManager == null) {
        return Map.of(true, List.of(), false, List.of());
    }

    var validator = new CategoryRecipeValidator<>(craftingCategory, ingredientManager, 9);

    List<RecipeHolder<TesselatingRecipe>> handled = new ArrayList<>();
    List<RecipeHolder<TesselatingRecipe>> unhandled = new ArrayList<>();

    List<RecipeHolder<TesselatingRecipe>> allRecipes = recipeManager.getAllRecipesFor(ModRecipeTypes.TESSELATING);
    for (RecipeHolder<TesselatingRecipe> recipe : allRecipes) {
        if (validator.isRecipeValid(recipe)) {
        if (validator.isRecipeHandled(recipe)) {
            handled.add(recipe);
        } else {
            unhandled.add(recipe);
        }
        }
    }
    return Map.of(
        true, handled,
        false, unhandled
    );
    }

//    public List<RecipeHolder<StonecutterRecipe>> getStonecuttingRecipes(IRecipeCategory<RecipeHolder<StonecutterRecipe>> stonecuttingCategory) {
//    var validator = new CategoryRecipeValidator<>(stonecuttingCategory, ingredientManager, 1);
//    return getValidHandledRecipes(recipeManager, RecipeType.STONECUTTING, validator);
//    }

    private static <C extends RecipeInput, T extends Recipe<C>> List<RecipeHolder<T>> getValidHandledRecipes(
    RecipeManager recipeManager,
    RecipeType<T> recipeType,
    CategoryRecipeValidator<T> validator
    ) {
    return recipeManager.getAllRecipesFor(recipeType)
        .stream()
        .filter(r -> validator.isRecipeValid(r) && validator.isRecipeHandled(r))
        .toList();
    }

    static class TesselatingRecipeExtension<T extends TesselatingRecipe> implements ITesselatingCategoryExtension<T> {
        @Override
        public void setRecipe(RecipeHolder<T> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
            var recipe = recipeHolder.value();
            var resultItem = RecipeUtil.getResultItem(recipe);

            var width = getWidth(recipeHolder);
            var height = getHeight(recipeHolder);
            craftingGridHelper.createAndSetOutputs(builder, List.of(resultItem));
            craftingGridHelper.createAndSetIngredients(builder, recipe.getIngredients(), width, height);
        }

        @SuppressWarnings("removal")
        @Override
        public Optional<ResourceLocation> getRegistryName(RecipeHolder<T> recipeHolder) {
            return Optional.of(recipeHolder.id());
        }

        @Override
        public int getWidth(RecipeHolder<T> recipeHolder) {
            var recipe = recipeHolder.value();
            if(recipe instanceof ShapedTesselatingRecipe shapedRecipe) {
                return shapedRecipe.getWidth();
            }

            return 0;
        }

        @Override
        public int getHeight(RecipeHolder<T> recipeHolder) {
            var recipe = recipeHolder.value();
            if(recipe instanceof ShapedTesselatingRecipe shapedRecipe) {
                return shapedRecipe.getHeight();
            }

            return 0;
        }

        @Override
        public boolean isHandled(RecipeHolder<T> recipe) {
            return !recipe.value().isSpecial();
        }
    }
}
