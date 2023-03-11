package org.dimdev.dimdoors.datagen;

import java.util.function.Consumer;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.dimdev.dimdoors.item.ModItems;

import static net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems;

public class TesselatingRecipeProvider {
	public static void generate(Consumer<FinishedRecipe> exporter) {
		TesselatingRecipeJsonBuilder.create(ModItems.STABLE_FABRIC).pattern("XX").pattern("XX").input('X', ModItems.WORLD_THREAD).unlockedBy("world_thread", hasItems(ModItems.WORLD_THREAD)).save(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.LIMINAL_LINT).pattern("XX").pattern("XX").input('X', ModItems.FRAYED_FILAMENTS).unlockedBy("frayed_filaments", hasItems(ModItems.FRAYED_FILAMENTS)).save(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.ENDURING_FIBERS).pattern("XX").pattern("XX").input('X', ModItems.INFRANGIBLE_FIBER).unlockedBy("infrangible_fiber", hasItems(ModItems.INFRANGIBLE_FIBER)).save(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.RIFT_PEARL).pattern("XO").input('X', Ingredient.of(ModItems.STABLE_FABRIC)).input('O', Items.ENDER_PEARL).unlockedBy("stable_fabric", hasItems(ModItems.STABLE_FABRIC)).save(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.FABRIC_OF_REALITY).pattern("XX").pattern("XO").input('O', ModItems.STABLE_FABRIC).input('X', ModItems.WORLD_THREAD).unlockedBy("stable_fabric", hasItems(ModItems.STABLE_FABRIC)).save(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.FUZZY_FIREBALL).pattern("XOX").input('X', ModItems.LIMINAL_LINT).input('O', Items.FIRE_CHARGE).unlockedBy("liminal_lint", hasItems(ModItems.LIMINAL_LINT)).save(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.GARMENT_OF_REALITY).pattern("XXX").pattern("XOX").pattern("XXX").input('X', ModItems.STABLE_FABRIC).input('O', ModItems.INFRANGIBLE_FIBER).unlockedBy("stable_fabric", hasItems(ModItems.STABLE_FABRIC)).save(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.FABRIC_OF_FINALITY).pattern("XOX").input('X', ModItems.ENDURING_FIBERS).input('O', Items.DRAGON_BREATH).unlockedBy("enduring_fabric", hasItems(ModItems.ENDURING_FIBERS)).save(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.REALITY_SPONGE).pattern("XOX").pattern("OXO").pattern("XOX").input('X', ModItems.STABLE_FABRIC).input('O', ModItems.INFRANGIBLE_FIBER).unlockedBy("liminal_lint", hasItems(ModItems.LIMINAL_LINT)).save(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.WORLD_THREAD_HELMET).pattern("XXX").pattern("X X").input('X', ModItems.WORLD_THREAD).unlockedBy("world_thread", hasItems(ModItems.WORLD_THREAD)).save(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.WORLD_THREAD_CHESTPLATE).pattern("X X").pattern("XXX").pattern("XXX").input('X', ModItems.WORLD_THREAD).unlockedBy("world_thread", hasItems(ModItems.WORLD_THREAD)).save(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.WORLD_THREAD_LEGGINGS).pattern("XXX").pattern("X X").pattern("X X").input('X', ModItems.WORLD_THREAD).unlockedBy("world_thread", hasItems(ModItems.WORLD_THREAD)).save(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.WORLD_THREAD_BOOTS).pattern("X X").pattern("X X").input('X', ModItems.WORLD_THREAD).unlockedBy("world_thread", hasItems(ModItems.WORLD_THREAD)).save(exporter);
	}
}
