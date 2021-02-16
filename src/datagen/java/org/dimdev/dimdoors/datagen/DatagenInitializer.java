package org.dimdev.dimdoors.datagen;

import java.nio.file.Paths;
import java.util.Map;

import me.shedaniel.cloth.api.datagen.v1.DataGeneratorHandler;
import me.shedaniel.cloth.api.datagen.v1.RecipeData;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

import net.minecraft.block.Block;
import net.minecraft.util.DyeColor;

public class DatagenInitializer {
	public static void main(String[] args) {
		ModBlocks.init();
		ModItems.init();
		DataGeneratorHandler handler = DataGeneratorHandler.create(Paths.get("./generated"));
		RecipeData recipes = handler.getRecipes();
		for (Map.Entry<DyeColor, Block> entry : ModBlocks.FABRIC_BLOCKS.entrySet()) {
			// TODO: add recipes for fabric
		}
	}
}
