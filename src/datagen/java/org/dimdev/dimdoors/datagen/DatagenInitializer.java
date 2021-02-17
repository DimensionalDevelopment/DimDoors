package org.dimdev.dimdoors.datagen;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

import net.minecraft.Bootstrap;
import net.minecraft.data.DataGenerator;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class DatagenInitializer implements PreLaunchEntrypoint {
	public static RecipeConsumer RECIPE_CONSUMER;

	// How to run the data generator:-
	// - Duplicate the Minecraft Client run config
	// - Change the module from DimensionalDoors.main to DimensionalDoors.datagen
	// - Profit
	@Override
	public void onPreLaunch() {
		try {
			Bootstrap.initialize();
			ModBlocks.init();
			ModItems.init();
			DataGenerator dataGenerator = new DataGenerator(Paths.get("./generated"), Collections.emptyList());
			dataGenerator.install(new FabricRecipeHandler(dataGenerator));
			dataGenerator.install(RECIPE_CONSUMER = new RecipeConsumer(dataGenerator));
			dataGenerator.run();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.exit(0);
	}
}
