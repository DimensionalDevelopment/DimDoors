package org.dimdev.dimdoors.datagen;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.tag.ModItemTags;

import java.util.function.Consumer;

public class DimdoorsRecipeProvider extends RecipeProvider {
	public DimdoorsRecipeProvider(PackOutput dataGenerator) {
		super(dataGenerator);
	}

	@Override
	public void buildRecipes(Consumer<FinishedRecipe> exporter) {
		//TODO: Find out proper RecipeCategory for these? I just random added this to make it work.
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.STONE_DOOR.get()).pattern("XX").pattern("XX").pattern("XX").define('X', Blocks.STONE).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.STONE)).save(exporter, DimensionalDoors.id("stone_door"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GOLD_DOOR.get(), 3).pattern("XX").pattern("XX").pattern("XX").define('X', ModItemTags.GOLD_INGOTS).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GOLD_INGOT)).save(exporter, DimensionalDoors.id("gold_door"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.QUARTZ_DOOR.get()).pattern("XX").pattern("XX").pattern("XX").define('X', Items.QUARTZ).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.QUARTZ)).save(exporter, DimensionalDoors.id("quartz_door"));
		TesselatingShapelessRecipeBuilder.shapeless(ModItems.RIFT_BLADE.get()).requires(Items.IRON_SWORD).requires(Items.ENDER_PEARL, 2).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_SWORD)).save(exporter, DimensionalDoors.id("rift_blade"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RIFT_REMOVER.get()).pattern(" # ").pattern("#X#").pattern(" # ").define('#', ModItemTags.GOLD_INGOTS).define('X', Items.ENDER_PEARL).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.RIFT_BLADE.get())).save(exporter, DimensionalDoors.id("rift_remover"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RIFT_REMOVER.get()).pattern("###").pattern("#X#").pattern("###").define('#', ModItemTags.GOLD_INGOTS).define('X', ModItems.STABLE_FABRIC.get()).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.STABLE_FABRIC.get())).save(exporter, DimensionalDoors.id("rift_remover_stable_fabric"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RIFT_SIGNATURE.get()).pattern(" # ").pattern("#X#").pattern(" # ").define('#', ModItemTags.IRON_INGOTS).define('X', Items.ENDER_PEARL).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.RIFT_BLADE.get())).save(exporter, DimensionalDoors.id("rift_signature"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RIFT_SIGNATURE.get()).pattern("###").pattern("#X#").pattern("###").define('#', ModItemTags.IRON_INGOTS).define('X', ModItems.STABLE_FABRIC.get()).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.STABLE_FABRIC.get())).save(exporter, DimensionalDoors.id("rift_signature_stable_fabric"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RIFT_STABILIZER.get()).pattern(" # ").pattern("#X#").pattern(" # ").define('#', ModItemTags.DIAMONDS).define('X', Items.ENDER_PEARL).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.RIFT_BLADE.get())).save(exporter, DimensionalDoors.id("rift_stabilizer"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RIFT_STABILIZER.get()).pattern("###").pattern("#X#").pattern("###").define('#', ModItemTags.DIAMONDS).define('X', ModItems.STABLE_FABRIC.get()).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.STABLE_FABRIC.get())).save(exporter, DimensionalDoors.id("rift_stabilizer_stable_fabric"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STABILIZED_RIFT_SIGNATURE.get()).pattern("# #").pattern(" X ").pattern("# #").define('#', Items.ENDER_PEARL).define('X', ModItems.RIFT_SIGNATURE.get()).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.RIFT_SIGNATURE.get())).save(exporter, DimensionalDoors.id("stabilized_rift_signature"));

		ColoredFabricRecipeProvider.generate(exporter);
		TesselatingRecipeProvider.generate(exporter);
	}
}
