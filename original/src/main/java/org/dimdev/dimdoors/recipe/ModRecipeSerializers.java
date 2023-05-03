package org.dimdev.dimdoors.recipe;

import net.minecraft.recipe.RecipeSerializer;

public class ModRecipeSerializers {
	public static RecipeSerializer<TesselatingRecipe> TESSELATING = RecipeSerializer.register("dimdoors:tesselating", new TesselatingRecipe.Serializer());

	public static void init() {
	}
}
