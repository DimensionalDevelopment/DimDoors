package datagen.org.dimdev.dimdoors.datagen;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

import java.util.function.Consumer;

import static net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems;

public class TesselatingRecipeProvider {
	public static void generate(Consumer<FinishedRecipe> exporter) {
		TesselatingRecipeJsonBuilder.create(ModItems.STABLE_FABRIC.get()).pattern("XX").pattern("XX").input('X', ModItems.WORLD_THREAD.get()).criterion("world_thread", TriggerInstance.hasItems(ModItems.WORLD_THREAD.get())).offerTo(exporter, DimensionalDoors.id("stable_fabric"));
		TesselatingRecipeJsonBuilder.create(ModItems.LIMINAL_LINT.get()).pattern("XX").pattern("XX").input('X', ModItems.FRAYED_FILAMENTS.get()).criterion("frayed_filaments", TriggerInstance.hasItems(ModItems.FRAYED_FILAMENTS.get())).offerTo(exporter, DimensionalDoors.id("liminal_lint"));
		TesselatingRecipeJsonBuilder.create(ModItems.ENDURING_FIBERS.get()).pattern("XX").pattern("XX").input('X', ModItems.INFRANGIBLE_FIBER.get()).criterion("infrangible_fiber", TriggerInstance.hasItems(ModItems.INFRANGIBLE_FIBER.get())).offerTo(exporter, DimensionalDoors.id("enduring_fibers"));
		TesselatingRecipeJsonBuilder.create(ModItems.RIFT_PEARL.get()).pattern("XO").input('X', Ingredient.of(ModItems.STABLE_FABRIC.get())).input('O', Items.ENDER_PEARL).criterion("stable_fabric", TriggerInstance.hasItems(ModItems.STABLE_FABRIC.get())).offerTo(exporter, DimensionalDoors.id("rift_pearl"));
		TesselatingRecipeJsonBuilder.create(ModItems.FABRIC_OF_REALITY.get()).pattern("XX").pattern("XO").input('O', ModItems.STABLE_FABRIC.get()).input('X', ModItems.WORLD_THREAD.get()).criterion("stable_fabric", TriggerInstance.hasItems(ModItems.STABLE_FABRIC.get())).offerTo(exporter, DimensionalDoors.id("fabric_of_reality"));
		TesselatingRecipeJsonBuilder.create(ModItems.FUZZY_FIREBALL.get()).pattern("XOX").input('X', ModItems.LIMINAL_LINT.get()).input('O', Items.FIRE_CHARGE).criterion("liminal_lint", TriggerInstance.hasItems(ModItems.LIMINAL_LINT.get())).offerTo(exporter, DimensionalDoors.id("fuzzy_fireball"));
//		TesselatingRecipeJsonBuilder.create(ModItems.GARMENT_OF_REALITY).pattern("XXX").pattern("XOX").pattern("XXX").input('X', ModItems.STABLE_FABRIC).input('O', ModItems.INFRANGIBLE_FIBER).criterion("stable_fabric", hasItems(ModItems.STABLE_FABRIC)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.FABRIC_OF_FINALITY.get()).pattern("XOX").input('X', ModItems.ENDURING_FIBERS.get()).input('O', Items.DRAGON_BREATH).criterion("enduring_fabric", TriggerInstance.hasItems(ModItems.ENDURING_FIBERS.get())).offerTo(exporter, DimensionalDoors.id("fabric_of_finality"));
		TesselatingRecipeJsonBuilder.create(ModBlocks.REALITY_SPONGE.get().asItem()).pattern("XOX").pattern("OXO").pattern("XOX").input('X', ModItems.STABLE_FABRIC.get()).input('O', ModItems.INFRANGIBLE_FIBER.get()).criterion("liminal_lint", TriggerInstance.hasItems(ModItems.LIMINAL_LINT.get())).offerTo(exporter, DimensionalDoors.id("reality_sponge"));
		TesselatingRecipeJsonBuilder.create(ModItems.WORLD_THREAD_HELMET.get().asItem()).pattern("XXX").pattern("X X").input('X', ModItems.WORLD_THREAD.get()).criterion("world_thread", TriggerInstance.hasItems(ModItems.WORLD_THREAD.get())).offerTo(exporter, DimensionalDoors.id("world_thread_helmet"));
		TesselatingRecipeJsonBuilder.create(ModItems.WORLD_THREAD_CHESTPLATE.get().asItem()).pattern("X X").pattern("XXX").pattern("XXX").input('X', ModItems.WORLD_THREAD.get()).criterion("world_thread", TriggerInstance.hasItems(ModItems.WORLD_THREAD.get())).offerTo(exporter, DimensionalDoors.id("world_thread_chestplate"));
		TesselatingRecipeJsonBuilder.create(ModItems.WORLD_THREAD_LEGGINGS.get().asItem()).pattern("XXX").pattern("X X").pattern("X X").input('X', ModItems.WORLD_THREAD.get()).criterion("world_thread", TriggerInstance.hasItems(ModItems.WORLD_THREAD.get())).offerTo(exporter, DimensionalDoors.id("world_thread_leggings"));
		TesselatingRecipeJsonBuilder.create(ModItems.WORLD_THREAD_BOOTS.get().asItem()).pattern("X X").pattern("X X").input('X', ModItems.WORLD_THREAD.get()).criterion("world_thread", TriggerInstance.hasItems(ModItems.WORLD_THREAD.get())).offerTo(exporter, DimensionalDoors.id("world_thread_boots"));
	}
}
