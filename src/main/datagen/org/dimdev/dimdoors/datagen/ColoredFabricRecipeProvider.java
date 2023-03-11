package org.dimdev.dimdoors.datagen;

import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

public class ColoredFabricRecipeProvider {
	public static void generate(Consumer<FinishedRecipe> exporter) {
		for (Map.Entry<DyeColor, Block> entry : ModBlocks.FABRIC_BLOCKS.entrySet()) {
			DyeColor dyeColor = entry.getKey();
			Block block = entry.getValue();
			ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block)
					.group("colored_fabric")
					.unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.WORLD_THREAD))
					.pattern(" X ")
					.pattern("XDX")
					.pattern(" X ")
					.define('X', ModItems.WORLD_THREAD)
					.define('D', DyeItem.byColor(dyeColor))
					.save(exporter, DimensionalDoors.id(dyeColor.getName() + "_fabric"));
		}
	}
}
