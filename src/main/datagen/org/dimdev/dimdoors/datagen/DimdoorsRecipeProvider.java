package org.dimdev.dimdoors.datagen;

import java.util.function.Consumer;

import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

import static org.dimdev.dimdoors.DimensionalDoors.id;

public class DimdoorsRecipeProvider extends FabricRecipeProvider {
	private static final TagKey<Item> GOLD_INGOTS = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "gold_ingots"));
	private static final TagKey<Item> IRON_INGOTS = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "iron_ingots"));
	private static final TagKey<Item> DIAMONDS = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "diamond"));

	public DimdoorsRecipeProvider(FabricDataOutput dataGenerator) {
		super(dataGenerator);
	}

	@Override
	public void generate(Consumer<RecipeJsonProvider> exporter) {
		//TODO: Find out proper RecipeCategory for these? I just random added this to make it work.
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.STONE_DOOR).pattern("XX").pattern("XX").pattern("XX").input('X', Blocks.STONE).criterion("inventory_changed", InventoryChangedCriterion.Conditions.items(Blocks.STONE)).offerTo(exporter, id("stone_door"));
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.GOLD_DOOR, 3).pattern("XX").pattern("XX").pattern("XX").input('X', GOLD_INGOTS).criterion("inventory_changed", InventoryChangedCriterion.Conditions.items(Items.GOLD_INGOT)).offerTo(exporter, id("gold_door"));
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.QUARTZ_DOOR).pattern("XX").pattern("XX").pattern("XX").input('X', Items.QUARTZ).criterion("inventory_changed", InventoryChangedCriterion.Conditions.items(Items.QUARTZ)).offerTo(exporter, id("quartz_door"));
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RIFT_BLADE).input(Items.IRON_SWORD).input(Items.ENDER_PEARL, 2).criterion("inventory_changed", InventoryChangedCriterion.Conditions.items(Items.IRON_SWORD)).offerTo(exporter, id("rift_blade"));
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RIFT_REMOVER).pattern(" # ").pattern("#X#").pattern(" # ").input('#', GOLD_INGOTS).input('X', Items.ENDER_PEARL).criterion("inventory_changed", InventoryChangedCriterion.Conditions.items(ModItems.RIFT_BLADE)).offerTo(exporter, id("rift_remover"));
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RIFT_REMOVER).pattern("###").pattern("#X#").pattern("###").input('#', GOLD_INGOTS).input('X', ModItems.STABLE_FABRIC).criterion("inventory_changed", InventoryChangedCriterion.Conditions.items(ModItems.STABLE_FABRIC)).offerTo(exporter, id("rift_remover_stable_fabric"));
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RIFT_SIGNATURE).pattern(" # ").pattern("#X#").pattern(" # ").input('#', IRON_INGOTS).input('X', Items.ENDER_PEARL).criterion("inventory_changed", InventoryChangedCriterion.Conditions.items(ModItems.RIFT_BLADE)).offerTo(exporter, id("rift_signature"));
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RIFT_SIGNATURE).pattern("###").pattern("#X#").pattern("###").input('#', IRON_INGOTS).input('X', ModItems.STABLE_FABRIC).criterion("inventory_changed", InventoryChangedCriterion.Conditions.items(ModItems.STABLE_FABRIC)).offerTo(exporter, id("rift_signature_stable_fabric"));
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RIFT_STABILIZER).pattern(" # ").pattern("#X#").pattern(" # ").input('#', DIAMONDS).input('X', Items.ENDER_PEARL).criterion("inventory_changed", InventoryChangedCriterion.Conditions.items(ModItems.RIFT_BLADE)).offerTo(exporter, id("rift_stabilizer"));
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RIFT_STABILIZER).pattern("###").pattern("#X#").pattern("###").input('#', DIAMONDS).input('X', ModItems.STABLE_FABRIC).criterion("inventory_changed", InventoryChangedCriterion.Conditions.items(ModItems.STABLE_FABRIC)).offerTo(exporter, id("rift_stabilizer_stable_fabric"));
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.STABILIZED_RIFT_SIGNATURE).pattern("# #").pattern(" X ").pattern("# #").input('#', Items.ENDER_PEARL).input('X', ModItems.RIFT_SIGNATURE).criterion("inventory_changed", InventoryChangedCriterion.Conditions.items(ModItems.RIFT_SIGNATURE)).offerTo(exporter, id("stabilized_rift_signature"));
	}
}
