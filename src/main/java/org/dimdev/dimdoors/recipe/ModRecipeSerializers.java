package org.dimdev.dimdoors.recipe;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.minecraft.world.item.crafting.RecipeSerializer;

import org.dimdev.dimdoors.Constants;

public class ModRecipeSerializers {
	private static DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Constants.MODID);

	public static RegistryObject<TesselatingRecipe.Serializer> TESSELATING = RECIPE_SERIALIZERS.register("tesselating", TesselatingRecipe.Serializer::new);

	public static void init(IEventBus bus) {
		RECIPE_SERIALIZERS.register(bus);
	}
}
