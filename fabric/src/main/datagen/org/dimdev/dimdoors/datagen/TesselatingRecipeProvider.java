package org.dimdev.dimdoors.datagen;

import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

public class TesselatingRecipeProvider {
	public static void generate(RecipeOutput exporter) {
		TesselatingRecipeJsonBuilder.create(ModItems.STABLE_FABRIC.get())
				.pattern("XX")
				.pattern("XX")
				.define('X', ModItems.WORLD_THREAD.get())
				.unlockedBy("world_thread", TriggerInstance.hasItems(ModItems.WORLD_THREAD.get()))
				.save(exporter, DimensionalDoors.id("stable_fabric"));
		TesselatingRecipeJsonBuilder.create(ModItems.LIMINAL_LINT.get())
				.pattern("XX")
				.pattern("XX")
				.define('X', ModItems.FRAYED_FILAMENTS.get())
				.unlockedBy("frayed_filaments", TriggerInstance.hasItems(ModItems.FRAYED_FILAMENTS.get()))
				.save(exporter, DimensionalDoors.id("liminal_lint"));
		TesselatingRecipeJsonBuilder.create(ModItems.ENDURING_FIBERS.get())
				.pattern("XX")
				.pattern("XX")
				.define('X', ModItems.INFRANGIBLE_FIBER.get())
				.unlockedBy("infrangible_fiber", TriggerInstance.hasItems(ModItems.INFRANGIBLE_FIBER.get()))
				.save(exporter, DimensionalDoors.id("enduring_fibers"));
		TesselatingRecipeJsonBuilder.create(ModItems.RIFT_PEARL.get())
				.pattern("XO")
				.define('X', Ingredient.of(ModItems.STABLE_FABRIC.get())).define('O', Items.ENDER_PEARL).unlockedBy("stable_fabric", TriggerInstance.hasItems(ModItems.STABLE_FABRIC.get())).save(exporter, DimensionalDoors.id("rift_pearl"));
		TesselatingRecipeJsonBuilder.create(ModBlocks.BLACK_FABRIC.get())
				.pattern("XX")
				.pattern("XO").define('O', ModItems.STABLE_FABRIC.get()).define('X', ModItems.WORLD_THREAD.get()).unlockedBy("stable_fabric", TriggerInstance.hasItems(ModItems.STABLE_FABRIC.get())).save(exporter, DimensionalDoors.id("fabric_of_reality"));
		TesselatingRecipeJsonBuilder.create(ModItems.FUZZY_FIREBALL.get())
				.pattern("XOX")
				.define('X', ModItems.LIMINAL_LINT.get())
				.define('O', Items.FIRE_CHARGE)
				.unlockedBy("liminal_lint", TriggerInstance.hasItems(ModItems.LIMINAL_LINT.get()))
				.save(exporter, DimensionalDoors.id("fuzzy_fireball"));
		TesselatingRecipeJsonBuilder.create(ModItems.FABRIC_OF_FINALITY.get())
				.pattern("XOX").define('X', ModItems.ENDURING_FIBERS.get())
				.define('O', Items.DRAGON_BREATH)
				.unlockedBy("enduring_fabric", TriggerInstance.hasItems(ModItems.ENDURING_FIBERS.get())).save(exporter, DimensionalDoors.id("fabric_of_finality"));
		TesselatingRecipeJsonBuilder.create(ModBlocks.REALITY_SPONGE.get().asItem())
				.pattern("XOX").
				pattern("OXO")
				.pattern("XOX").define('X', ModItems.STABLE_FABRIC.get()).define('O', ModItems.INFRANGIBLE_FIBER.get()).unlockedBy("liminal_lint", TriggerInstance.hasItems(ModItems.LIMINAL_LINT.get())).save(exporter, DimensionalDoors.id("reality_sponge"));
		TesselatingRecipeJsonBuilder.create(ModItems.WORLD_THREAD_HELMET.get().asItem())
				.pattern("XXX").
				pattern("X X")
				.define('X', ModItems.WORLD_THREAD.get()).unlockedBy("world_thread", TriggerInstance.hasItems(ModItems.WORLD_THREAD.get())).save(exporter, DimensionalDoors.id("world_thread_helmet"));
		TesselatingRecipeJsonBuilder.create(ModItems.WORLD_THREAD_CHESTPLATE.get().asItem())
				.pattern("X X")
				.pattern("XXX")
				.pattern("XXX")
				.define('X', ModItems.WORLD_THREAD.get())
				.unlockedBy("world_thread", TriggerInstance.hasItems(ModItems.WORLD_THREAD.get()))
				.save(exporter, DimensionalDoors.id("world_thread_chestplate"));
		TesselatingRecipeJsonBuilder.create(ModItems.WORLD_THREAD_LEGGINGS.get().asItem())
				.pattern("XXX").
				pattern("X X")
				.pattern("X X")
				.define('X', ModItems.WORLD_THREAD.get())
				.unlockedBy("world_thread", TriggerInstance.hasItems(ModItems.WORLD_THREAD.get()))
				.save(exporter, DimensionalDoors.id("world_thread_leggings"));
		TesselatingRecipeJsonBuilder.create(ModItems.WORLD_THREAD_BOOTS.get().asItem())
				.pattern("X X")
				.pattern("X X")
				.define('X', ModItems.WORLD_THREAD.get())
				.unlockedBy("world_thread", TriggerInstance.hasItems(ModItems.WORLD_THREAD.get()))
				.save(exporter, DimensionalDoors.id("world_thread_boots"));

		TesselatingRecipeJsonBuilder.create(ModItems.GARMENT_OF_REALITY_HELMET.get())
		  		.pattern("XXX")
				.pattern("XOX")
				.define('X', ModItems.STABLE_FABRIC.get())
				.define('O', ModItems.INFRANGIBLE_FIBER.get())
				.unlockedBy("infrangible_fiber", TriggerInstance.hasItems(ModItems.INFRANGIBLE_FIBER.get()))
				.save(exporter, DimensionalDoors.id("garment_of_reality_helmet"));
		TesselatingRecipeJsonBuilder.create(ModItems.GARMENT_OF_REALITY_CHESTPLATE.get())
				.pattern("XOX")
				.pattern("XXX")
				.pattern("XXX")
				.define('X', ModItems.STABLE_FABRIC.get())
				.define('O', ModItems.INFRANGIBLE_FIBER.get())
				.unlockedBy("infrangible_fiber", TriggerInstance.hasItems(ModItems.INFRANGIBLE_FIBER.get()))
				.save(exporter, DimensionalDoors.id("garment_of_reality_chestplate"));
		TesselatingRecipeJsonBuilder.create(ModItems.GARMENT_OF_REALITY_LEGGINGS.get())
				.pattern("XXX")
				.pattern("XOX")
				.pattern("X X")
				.define('X', ModItems.STABLE_FABRIC.get())
				.define('O', ModItems.INFRANGIBLE_FIBER.get())
				.unlockedBy("infrangible_fiber", TriggerInstance.hasItems(ModItems.INFRANGIBLE_FIBER.get()))
				.save(exporter, DimensionalDoors.id("garment_of_reality_leggings"));
		TesselatingRecipeJsonBuilder.create(ModItems.GARMENT_OF_REALITY_BOOTS.get())
				.pattern("X X")
				.pattern("XOX")
				.define('X', ModItems.STABLE_FABRIC.get())
				.define('O', ModItems.INFRANGIBLE_FIBER.get())
				.unlockedBy("infrangible_fiber", TriggerInstance.hasItems(ModItems.INFRANGIBLE_FIBER.get()))
				.save(exporter, DimensionalDoors.id("garment_of_reality_boots"));

	}
}
