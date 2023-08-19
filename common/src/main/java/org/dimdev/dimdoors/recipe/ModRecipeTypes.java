package org.dimdev.dimdoors.recipe;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;

import org.dimdev.dimdoors.DimensionalDoors;

public class ModRecipeTypes {
	public static final DeferredRegister<RecipeType<?>> RECIPES_TYPES = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.RECIPE_TYPE);
	public static RegistrySupplier<RecipeType<TesselatingRecipe>> TESSELATING = register("tesselating");

	private static <T extends ShapedRecipe> RegistrySupplier<RecipeType<T>> register(String name) {
		var id = DimensionalDoors.id(name);
		return RECIPES_TYPES.register(id, () -> new RecipeType<T>() {
			@Override
			public String toString() {
				return id.toString();
			}
		});
	} ;

	public static void init() {
		RECIPES_TYPES.register();
	}
}
