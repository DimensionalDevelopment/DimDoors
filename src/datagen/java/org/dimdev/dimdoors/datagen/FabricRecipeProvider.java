package org.dimdev.dimdoors.datagen;

import java.util.Map;

import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Block;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.item.DyeItem;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import static org.dimdev.dimdoors.datagen.DatagenInitializer.RECIPE_CONSUMER;

public class FabricRecipeProvider implements DataProvider {
	private final DataGenerator dataGenerator;

	public FabricRecipeProvider(DataGenerator dataGenerator) {
		this.dataGenerator = dataGenerator;
	}

	@Override
	public void run(DataCache cache) {
		for (Map.Entry<DyeColor, Block> entry : ModBlocks.FABRIC_BLOCKS.entrySet()) {
			DyeColor dyeColor = entry.getKey();
			Block block = entry.getValue();
			ShapedRecipeJsonFactory.create(block)
					.group("colored_fabric")
					.criterion("inventory_changed", InventoryChangedCriterion.Conditions.items(ModItems.WORLD_THREAD))
					.pattern(" X ")
					.pattern("XDX")
					.pattern(" X ")
					.input('X', ModItems.WORLD_THREAD)
					.input('D', DyeItem.byColor(dyeColor))
					.offerTo(RECIPE_CONSUMER, new Identifier("dimdoors", dyeColor.getName() + "_fabric"));
		}
	}

	@Override
	public String getName() {
		return "Colored Fabric Recipes";
	}
}
