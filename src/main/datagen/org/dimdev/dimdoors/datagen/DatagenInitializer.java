package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DatagenInitializer implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
		dataGenerator.addProvider(new FabricRecipeProvider(dataGenerator));
		dataGenerator.addProvider(new AdvancementProvider(dataGenerator));
		dataGenerator.addProvider(new LootTableProvider(dataGenerator));
		dataGenerator.addProvider(new LimboDecayProvider(dataGenerator));
	}
}
