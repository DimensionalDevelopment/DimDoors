package org.dimdev.dimdoors.compat.jei.tesselating

import org.dimdev.dimdoors.recipe.TesselatingRecipe

interface IExtendableRksRecipeCategory {
    fun <R : TesselatingRecipe> addExtension(clazz: Class<out R>, var2: ITesselatingCategoryExtension<R>)
}