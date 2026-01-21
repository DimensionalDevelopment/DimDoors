package org.dimdev.dimdoors.compat.jei.tesselating;

import org.dimdev.dimdoors.recipe.TesselatingRecipe;

public interface IExtendableRksRecipeCategory {
    <R extends TesselatingRecipe> void addExtension(Class<? extends R> clazz, ITesselatingCategoryExtension<R> var2);
}