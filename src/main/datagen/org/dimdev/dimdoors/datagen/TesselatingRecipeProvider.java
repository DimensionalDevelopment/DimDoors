package org.dimdev.dimdoors.datagen;

import java.util.function.Consumer;

import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;

import org.dimdev.dimdoors.item.ModItems;

import static net.minecraft.advancement.criterion.InventoryChangedCriterion.Conditions.items;

public class TesselatingRecipeProvider {
	public static void generate(Consumer<RecipeJsonProvider> exporter) {
		TesselatingRecipeJsonBuilder.create(ModItems.STABLE_FABRIC).pattern("XX").pattern("XX").input('X', ModItems.WORLD_THREAD).criterion("world_thread", items(ModItems.WORLD_THREAD)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.LIMINAL_LINT).pattern("XX").pattern("XX").input('X', ModItems.FRAYED_FILAMENTS).criterion("frayed_filaments", items(ModItems.FRAYED_FILAMENTS)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.ENDURING_FIBERS).pattern("XX").pattern("XX").input('X', ModItems.INFRANGIBLE_FIBER).criterion("infrangible_fiber", items(ModItems.INFRANGIBLE_FIBER)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.RIFT_PEARL).pattern("XO").input('X', Ingredient.ofItems(ModItems.STABLE_FABRIC)).input('O', Items.ENDER_PEARL).criterion("stable_fabric", items(ModItems.STABLE_FABRIC)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.FABRIC_OF_REALITY).pattern("XX").pattern("XO").input('O', ModItems.STABLE_FABRIC).input('X', ModItems.WORLD_THREAD).criterion("stable_fabric", items(ModItems.STABLE_FABRIC)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.FUZZY_FIREBALL).pattern("XOX").input('X', ModItems.LIMINAL_LINT).input('O', Items.FIRE_CHARGE).criterion("liminal_lint", items(ModItems.LIMINAL_LINT)).offerTo(exporter);
//		TesselatingRecipeJsonBuilder.create(ModItems.GARMENT_OF_REALITY).pattern("XXX").pattern("XOX").pattern("XXX").input('X', ModItems.STABLE_FABRIC).input('O', ModItems.INFRANGIBLE_FIBER).criterion("stable_fabric", items(ModItems.STABLE_FABRIC)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.FABRIC_OF_FINALITY).pattern("XOX").input('X', ModItems.ENDURING_FIBERS).input('O', Items.DRAGON_BREATH).criterion("enduring_fabric", items(ModItems.ENDURING_FIBERS)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.REALITY_SPONGE).pattern("XOX").pattern("OXO").pattern("XOX").input('X', ModItems.STABLE_FABRIC).input('O', ModItems.INFRANGIBLE_FIBER).criterion("liminal_lint", items(ModItems.LIMINAL_LINT)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.WORLD_THREAD_HELMET).pattern("XXX").pattern("X X").input('X', ModItems.WORLD_THREAD).criterion("world_thread", items(ModItems.WORLD_THREAD)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.WORLD_THREAD_CHESTPLATE).pattern("X X").pattern("XXX").pattern("XXX").input('X', ModItems.WORLD_THREAD).criterion("world_thread", items(ModItems.WORLD_THREAD)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.WORLD_THREAD_LEGGINGS).pattern("XXX").pattern("X X").pattern("X X").input('X', ModItems.WORLD_THREAD).criterion("world_thread", items(ModItems.WORLD_THREAD)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.WORLD_THREAD_BOOTS).pattern("X X").pattern("X X").input('X', ModItems.WORLD_THREAD).criterion("world_thread", items(ModItems.WORLD_THREAD)).offerTo(exporter);
	}
}
