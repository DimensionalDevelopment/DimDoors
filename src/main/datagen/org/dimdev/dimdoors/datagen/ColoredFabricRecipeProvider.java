package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.DyeItem;
import net.minecraft.util.DyeColor;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

import java.util.Map;
import java.util.function.Consumer;

public class ColoredFabricRecipeProvider extends net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider {

	public ColoredFabricRecipeProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
		for (Map.Entry<DyeColor, Block> entry : ModBlocks.FABRIC_BLOCKS.entrySet()) {
			DyeColor dyeColor = entry.getKey();
			Block block = entry.getValue();
			ShapedRecipeJsonBuilder.create(block)
					.group("colored_fabric")
					.criterion("inventory_changed", InventoryChangedCriterion.Conditions.items(ModItems.WORLD_THREAD))
					.pattern(" X ")
					.pattern("XDX")
					.pattern(" X ")
					.input('X', ModItems.WORLD_THREAD)
					.input('D', DyeItem.byColor(dyeColor))
					.offerTo(exporter, DimensionalDoors.id(dyeColor.getName() + "_fabric"));
		}
	}

	@Override
	public String getName() {
		return "Colored Fabric Recipes";
	}
}
