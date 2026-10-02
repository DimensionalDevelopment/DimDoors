package org.dimdev.dimdoors.compat.jei.tesselating

import mezz.jei.api.recipe.category.IRecipeCategory
import mezz.jei.api.runtime.IIngredientManager
import mezz.jei.library.plugins.vanilla.crafting.CategoryRecipeValidator
import net.minecraft.client.Minecraft
import net.minecraft.world.item.crafting.RecipeHolder
import org.dimdev.dimdoors.recipe.ModRecipeTypes
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
}
