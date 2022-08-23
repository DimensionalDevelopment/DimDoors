package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DatagenInitializer implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
		dataGenerator.addProvider(BlockStateProvider::new);
		dataGenerator.addProvider(FabricRecipeProvider::new);
		dataGenerator.addProvider(AdvancementProvider::new);
		dataGenerator.addProvider(LootTableProvider::new);
		dataGenerator.addProvider(LimboDecayProvider::new);
	}
}
