package org.dimdev.dimdoors.recipe;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

import org.dimdev.dimdoors.Constants;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModRecipeTypes {
	private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Constants.MODID);

	public static RegistryObject<RecipeType<TesselatingRecipe>> TESSELATING = RECIPE_TYPES.register("tesselating", () -> RecipeType.<TesselatingRecipe>simple(DimensionalDoors.resource("tesselating")));

	public static void init(IEventBus bus) {
		RECIPE_TYPES.register(bus);
	}
}
