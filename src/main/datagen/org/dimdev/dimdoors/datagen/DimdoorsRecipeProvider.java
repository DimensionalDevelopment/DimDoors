package org.dimdev.dimdoors.datagen;

import java.util.function.Consumer;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

import static org.dimdev.dimdoors.DimensionalDoors.resource;

public class DimdoorsRecipeProvider extends FabricRecipeProvider {
	private static final TagKey<Item> GOLD_INGOTS = TagKey.create(Registries.ITEM, new ResourceLocation("c", "gold_ingots"));
	private static final TagKey<Item> IRON_INGOTS = TagKey.create(Registries.ITEM, new ResourceLocation("c", "iron_ingots"));
	private static final TagKey<Item> DIAMONDS = TagKey.create(Registries.ITEM, new ResourceLocation("c", "diamond"));

	public DimdoorsRecipeProvider(FabricDataOutput dataGenerator) {
		super(dataGenerator);
	}

	@Override
	public void buildRecipes(Consumer<FinishedRecipe> exporter) {
		//TODO: Find out proper RecipeCategory for these? I just random added this to make it work.
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.STONE_DOOR).pattern("XX").pattern("XX").pattern("XX").define('X', Blocks.STONE).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.STONE)).save(exporter, resource("stone_door"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GOLD_DOOR, 3).pattern("XX").pattern("XX").pattern("XX").define('X', GOLD_INGOTS).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GOLD_INGOT)).save(exporter, resource("gold_door"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.QUARTZ_DOOR).pattern("XX").pattern("XX").pattern("XX").define('X', Items.QUARTZ).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.QUARTZ)).save(exporter, resource("quartz_door"));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.RIFT_BLADE).requires(Items.IRON_SWORD).requires(Items.ENDER_PEARL, 2).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_SWORD)).save(exporter, resource("rift_blade"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RIFT_REMOVER).pattern(" # ").pattern("#X#").pattern(" # ").define('#', GOLD_INGOTS).define('X', Items.ENDER_PEARL).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.RIFT_BLADE)).save(exporter, resource("rift_remover"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RIFT_REMOVER).pattern("###").pattern("#X#").pattern("###").define('#', GOLD_INGOTS).define('X', ModItems.STABLE_FABRIC).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.STABLE_FABRIC)).save(exporter, resource("rift_remover_stable_fabric"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RIFT_SIGNATURE).pattern(" # ").pattern("#X#").pattern(" # ").define('#', IRON_INGOTS).define('X', Items.ENDER_PEARL).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.RIFT_BLADE)).save(exporter, resource("rift_signature"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RIFT_SIGNATURE).pattern("###").pattern("#X#").pattern("###").define('#', IRON_INGOTS).define('X', ModItems.STABLE_FABRIC).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.STABLE_FABRIC)).save(exporter, resource("rift_signature_stable_fabric"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RIFT_STABILIZER).pattern(" # ").pattern("#X#").pattern(" # ").define('#', DIAMONDS).define('X', Items.ENDER_PEARL).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.RIFT_BLADE)).save(exporter, resource("rift_stabilizer"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RIFT_STABILIZER).pattern("###").pattern("#X#").pattern("###").define('#', DIAMONDS).define('X', ModItems.STABLE_FABRIC).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.STABLE_FABRIC)).save(exporter, resource("rift_stabilizer_stable_fabric"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STABILIZED_RIFT_SIGNATURE).pattern("# #").pattern(" X ").pattern("# #").define('#', Items.ENDER_PEARL).define('X', ModItems.RIFT_SIGNATURE).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.RIFT_SIGNATURE)).save(exporter, resource("stabilized_rift_signature"));

		ColoredFabricRecipeProvider.generate(exporter);
		TesselatingRecipeProvider.generate(exporter);
	}
}
