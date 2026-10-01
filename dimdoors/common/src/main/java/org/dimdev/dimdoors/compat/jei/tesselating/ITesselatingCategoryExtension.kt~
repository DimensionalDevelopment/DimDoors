package org.dimdev.dimdoors.compat.jei.tesselating

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.ingredient.ICraftingGridHelper
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.RecipeHolder
import org.dimdev.dimdoors.recipe.TesselatingRecipe

interface ITesselatingCategoryExtension<T : TesselatingRecipe?> : IRecipeCategoryExtension<RecipeHolder<T?>?> {
    fun setRecipe(
        recipeHolder: RecipeHolder<T>,
        builder: IRecipeLayoutBuilder,
        craftingGridHelper: ICraftingGridHelper,
        focuses: IFocusGroup
    ) {
        this.setRecipe(builder, craftingGridHelper, focuses)
    }

    fun onDisplayedIngredientsUpdate(
        recipeHolder: RecipeHolder<T>,
        recipeSlots: MutableList<IRecipeSlotDrawable>,
        focuses: IFocusGroup
    ) {
    }

    @Deprecated("")
    fun getRegistryName(recipeHolder: RecipeHolder<T>): ResourceLocation? {
        return this.registryName ?: recipeHolder.id
    }

    fun getWidth(recipeHolder: RecipeHolder<T>): Int {
        return this.width
    }

    fun getHeight(recipeHolder: RecipeHolder<T>): Int {
        return this.height
    }

    @Deprecated("")
    fun setRecipe(builder: IRecipeLayoutBuilder, craftingGridHelper: ICraftingGridHelper, focuses: IFocusGroup) {
    }

    @get:Deprecated("")
    val registryName: ResourceLocation? get() = null

    @get:Deprecated("") val width: Int get() = 0

    @get:Deprecated("") val height: Int get() = 0
}