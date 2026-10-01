package org.dimdev.dimdoors.compat.jei.tesselating

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.ingredient.ICraftingGridHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.category.IRecipeCategory
import mezz.jei.api.runtime.IIngredientManager
import mezz.jei.library.plugins.vanilla.crafting.CategoryRecipeValidator
import mezz.jei.library.util.RecipeUtil
import net.minecraft.client.Minecraft
import net.minecraft.world.item.crafting.*
import org.dimdev.dimcore.api.castOrNull
import org.dimdev.dimdoors.recipe.ModRecipeTypes
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe
import org.dimdev.dimdoors.recipe.TesselatingRecipe

class DimDoorsRecipes(private val ingredientManager: IIngredientManager) {
    private val recipeManager = Minecraft.getInstance().level?.recipeManager

    fun getTesselating(craftingCategory: IRecipeCategory<RecipeHolder<TesselatingRecipe>>): MutableMap<Boolean, MutableList<RecipeHolder<TesselatingRecipe>>> {
        if (recipeManager == null) {
            return mutableMapOf(true to mutableListOf(), false to mutableListOf())
        }

        val validator = CategoryRecipeValidator(craftingCategory, ingredientManager, 9)

        val handled = mutableListOf<RecipeHolder<TesselatingRecipe>>()
        val unhandled = mutableListOf<RecipeHolder<TesselatingRecipe>>()

        val allRecipes = recipeManager.getAllRecipesFor(ModRecipeTypes.TESSELATING)
        for (recipe in allRecipes) {
            if (validator.isRecipeValid(recipe)) {
                if (validator.isRecipeHandled(recipe)) {
                    handled.add(recipe)
                } else {
                    unhandled.add(recipe)
                }
            }
        }

        return mutableMapOf(
            true to handled,
            false to unhandled
        )
    }

    internal class TesselatingRecipeExtension<T : TesselatingRecipe> : ITesselatingCategoryExtension<T> {
        override fun setRecipe(
            recipeHolder: RecipeHolder<T>,
            builder: IRecipeLayoutBuilder,
            craftingGridHelper: ICraftingGridHelper,
            focuses: IFocusGroup
        ) {
            val recipe = recipeHolder.value()
            val resultItem = RecipeUtil.getResultItem(recipe)

            val width = getWidth(recipeHolder)
            val height = getHeight(recipeHolder)
            craftingGridHelper.createAndSetOutputs(builder, mutableListOf(resultItem))
            craftingGridHelper.createAndSetIngredients(builder, recipe.ingredients, width, height)
        }

        override fun getRegistryName(recipeHolder: RecipeHolder<T>) = recipeHolder.id()

        override fun getWidth(recipeHolder: RecipeHolder<T>) = recipeHolder.value().castOrNull<ShapedTesselatingRecipe>()?.width ?: 0

        override fun getHeight(recipeHolder: RecipeHolder<T>) = recipeHolder.value().castOrNull<ShapedTesselatingRecipe>()?.height ?: 0

        override fun isHandled(recipe: RecipeHolder<T>) = !recipe.value().isSpecial
    }

    companion object {
        //    public List<RecipeHolder<StonecutterRecipe>> getStonecuttingRecipes(IRecipeCategory<RecipeHolder<StonecutterRecipe>> stonecuttingCategory) {
        //    var validator = new CategoryRecipeValidator<>(stonecuttingCategory, ingredientManager, 1);
        //    return getValidHandledRecipes(recipeManager, RecipeType.STONECUTTING, validator);
        //    }
        private fun <C : RecipeInput, T : Recipe<C>> getValidHandledRecipes(
            recipeManager: RecipeManager,
            recipeType: RecipeType<T>,
            validator: CategoryRecipeValidator<T>
        ): List<RecipeHolder<T>> {
            return recipeManager.getAllRecipesFor<C, T>(recipeType)
                .filter { r -> validator.isRecipeValid(r) && validator.isRecipeHandled(r) }
                .toList()
        }
    }
}
