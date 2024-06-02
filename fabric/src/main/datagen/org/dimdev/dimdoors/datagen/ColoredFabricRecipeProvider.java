package org.dimdev.dimdoors.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.forge.item.ModItems;

import java.util.Map;
import java.util.function.Consumer;

public class ColoredFabricRecipeProvider {
	public static void generate(Consumer<FinishedRecipe> exporter) {
		for (Map.Entry<DyeColor, RegistrySupplier<Block>> entry : ModBlocks.FABRIC_BLOCKS.entrySet()) {
			DyeColor dyeColor = entry.getKey();
			Block block = entry.getValue().get();
			ShapedRecipeBuilder.shaped(block, 1)
					.group("colored_fabric")
					.unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.WORLD_THREAD.get()))
					.pattern(" X ")
					.pattern("XDX")
					.pattern(" X ")
					.define('X', ModItems.WORLD_THREAD.get())
					.define('D', DyeItem.byColor(dyeColor))
					.save(exporter, DimensionalDoors.id(dyeColor.getName() + "_fabric"));
		}
	}
}
