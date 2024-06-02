package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.forge.item.ModItems;
import org.dimdev.dimdoors.tag.ModItemTags;

import java.util.function.Consumer;

public class DimdoorsRecipeProvider extends FabricRecipeProvider {
	public DimdoorsRecipeProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void generateRecipes(Consumer<FinishedRecipe> exporter) {
		ShapedRecipeBuilder.shaped(ModBlocks.STONE_DOOR.get()).pattern("XX").pattern("XX").pattern("XX").define('X', Blocks.STONE).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.STONE)).save(exporter, DimensionalDoors.id("stone_door"));
		ShapedRecipeBuilder.shaped(ModBlocks.GOLD_DOOR.get(), 3).pattern("XX").pattern("XX").pattern("XX").define('X', ModItemTags.GOLD_INGOTS).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GOLD_INGOT)).save(exporter, DimensionalDoors.id("gold_door"));
		ShapedRecipeBuilder.shaped(ModBlocks.QUARTZ_DOOR.get()).pattern("XX").pattern("XX").pattern("XX").define('X', Items.QUARTZ).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.QUARTZ)).save(exporter, DimensionalDoors.id("quartz_door"));
		TesselatingShapelessRecipeBuilder.shapeless(ModItems.RIFT_BLADE.get()).requires(Items.IRON_SWORD).requires(Items.ENDER_PEARL, 2).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_SWORD)).save(exporter, DimensionalDoors.id("rift_blade"));
		ShapedRecipeBuilder.shaped(ModItems.RIFT_REMOVER.get()).pattern(" # ").pattern("#X#").pattern(" # ").define('#', ModItemTags.GOLD_INGOTS).define('X', Items.ENDER_PEARL).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.RIFT_BLADE.get())).save(exporter, DimensionalDoors.id("rift_remover"));
		ShapedRecipeBuilder.shaped(ModItems.RIFT_REMOVER.get()).pattern("###").pattern("#X#").pattern("###").define('#', ModItemTags.GOLD_INGOTS).define('X', ModItems.STABLE_FABRIC.get()).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.STABLE_FABRIC.get())).save(exporter, DimensionalDoors.id("rift_remover_stable_fabric"));
		ShapedRecipeBuilder.shaped(ModItems.RIFT_SIGNATURE.get()).pattern(" # ").pattern("#X#").pattern(" # ").define('#', ModItemTags.IRON_INGOTS).define('X', Items.ENDER_PEARL).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.RIFT_BLADE.get())).save(exporter, DimensionalDoors.id("rift_signature"));
		ShapedRecipeBuilder.shaped(ModItems.RIFT_SIGNATURE.get()).pattern("###").pattern("#X#").pattern("###").define('#', ModItemTags.IRON_INGOTS).define('X', ModItems.STABLE_FABRIC.get()).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.STABLE_FABRIC.get())).save(exporter, DimensionalDoors.id("rift_signature_stable_fabric"));
		ShapedRecipeBuilder.shaped(ModItems.RIFT_STABILIZER.get()).pattern(" # ").pattern("#X#").pattern(" # ").define('#', ModItemTags.DIAMONDS).define('X', Items.ENDER_PEARL).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.RIFT_BLADE.get())).save(exporter, DimensionalDoors.id("rift_stabilizer"));
		ShapedRecipeBuilder.shaped(ModItems.RIFT_STABILIZER.get()).pattern("###").pattern("#X#").pattern("###").define('#', ModItemTags.DIAMONDS).define('X', ModItems.STABLE_FABRIC.get()).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.STABLE_FABRIC.get())).save(exporter, DimensionalDoors.id("rift_stabilizer_stable_fabric"));
		ShapedRecipeBuilder.shaped(ModItems.STABILIZED_RIFT_SIGNATURE.get()).pattern("# #").pattern(" X ").pattern("# #").define('#', Items.ENDER_PEARL).define('X', ModItems.RIFT_SIGNATURE.get()).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.RIFT_SIGNATURE.get())).save(exporter, DimensionalDoors.id("stabilized_rift_signature"));
		ShapedRecipeBuilder.shaped(ModBlocks.TESSELATING_LOOM.get())
				.pattern("XOX")
				.pattern("ALA")
				.pattern("XAX")
				.define('A', ModItems.WORLD_THREAD.get())
				.define('L', Blocks.LOOM)
				.define('X', Blocks.SCAFFOLDING)
				.define('O', ModBlocks.fabricFromDye(DyeColor.BLACK).get())
				.unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LOOM))
				.save(exporter, DimensionalDoors.id("tesselating_loom"));

		ColoredFabricRecipeProvider.generate(exporter);
		TesselatingRecipeProvider.generate(exporter);
	}
}
