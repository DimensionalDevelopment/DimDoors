package org.dimdev.dimdoors.recipe;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.dimdev.dimdoors.DimensionalDoors;

import java.util.function.Supplier;

public class ModRecipeSerializers {
	public static DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.RECIPE_SERIALIZER);
	public static RegistrySupplier<RecipeSerializer<ShapedTesselatingRecipe>> SHAPED_TESSELATING = register("shaped_tesselating", ShapedTesselatingRecipe.Serializer::new);
	public static RegistrySupplier<RecipeSerializer<TesselatingShapelessRecipe>> SHAPELESS_TESSELATING = register("shapeless_tesselating", TesselatingShapelessRecipe.Serializer::new);

	public static void init() {
		RECIPE_SERIALIZERS.register();
	}

	public static <T extends Recipe<?>> RegistrySupplier<RecipeSerializer<T>> register(String name, Supplier<RecipeSerializer<T>> supplier) {
		return RECIPE_SERIALIZERS.register(name, supplier);
	}

}
