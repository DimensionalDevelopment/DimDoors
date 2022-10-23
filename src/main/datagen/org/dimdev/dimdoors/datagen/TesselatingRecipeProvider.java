package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import org.dimdev.dimdoors.item.ModItems;

import java.util.function.Consumer;

public class TesselatingRecipeProvider extends net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider {

	public TesselatingRecipeProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {

		TesselatingRecipeJsonBuilder.create(ModItems.STABLE_FABRIC).input(ModItems.WORLD_THREAD, 4).criterion("world_thread", InventoryChangedCriterion.Conditions.items(ModItems.WORLD_THREAD)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.LIMINAL_LINT).input(ModItems.FRAYED_FILAMENTS, 4).criterion("frayed_filaments", InventoryChangedCriterion.Conditions.items(ModItems.FRAYED_FILAMENTS)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.ENDURING_FIBERS).input(ModItems.INFRANGIBLE_FIBER, 4).criterion("infrangible_fiber", InventoryChangedCriterion.Conditions.items(ModItems.INFRANGIBLE_FIBER)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.RIFT_PEARL).input(Ingredient.ofItems(ModItems.STABLE_FABRIC)).input(Items.ENDER_PEARL).criterion("stable_fabric", InventoryChangedCriterion.Conditions.items(ModItems.STABLE_FABRIC)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.FABRIC_OF_REALITY).input(ModItems.STABLE_FABRIC).input(ModItems.WORLD_THREAD, 3).criterion("stable_fabric", InventoryChangedCriterion.Conditions.items(ModItems.STABLE_FABRIC)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.FUZZY_FIREBALL).input(ModItems.LIMINAL_LINT, 2).input(Items.FIRE_CHARGE).criterion("liminal_lint", InventoryChangedCriterion.Conditions.items(ModItems.LIMINAL_LINT)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.GARMENT_OF_REALITY).input(ModItems.STABLE_FABRIC, 8).input(ModItems.INFRANGIBLE_FIBER, 2).criterion("stable_fabric", InventoryChangedCriterion.Conditions.items(ModItems.STABLE_FABRIC)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.FABRIC_OF_FINALITY).input(ModItems.ENDURING_FIBERS, 2).input(Items.DRAGON_BREATH).criterion("enduring_fabric", InventoryChangedCriterion.Conditions.items(ModItems.ENDURING_FIBERS)).offerTo(exporter);
		TesselatingRecipeJsonBuilder.create(ModItems.REALITY_SPONGE).input(ModItems.STABLE_FABRIC, 5).input(ModItems.INFRANGIBLE_FIBER, 4).criterion("liminal_lint", InventoryChangedCriterion.Conditions.items(ModItems.LIMINAL_LINT)).offerTo(exporter);
	}
}
