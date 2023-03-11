package org.dimdev.dimdoors.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipeSerializers {
	public static RecipeSerializer<TesselatingRecipe> TESSELATING = RecipeSerializer.register("dimdoors:tesselating", new TesselatingRecipe.Serializer());

	public static void init() {
	}
}
