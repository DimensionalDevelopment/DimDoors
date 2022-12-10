package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import org.dimdev.dimdoors.DimensionalDoors;

public class DatagenInitializer implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
		FabricDataGenerator.Pack pack = dataGenerator.createBuiltinResourcePack(DimensionalDoors.id("dimdoors"));

		pack.addProvider(BlockStateProvider::new);
		pack.addProvider(DimdoorsRecipeProvider::new);
		pack.addProvider(ColoredFabricRecipeProvider::new);
		pack.addProvider(AdvancementProvider::new);
		pack.addProvider(LootTableProvider::new);
		pack.addProvider(LimboDecayProvider::new);
		pack.addProvider(BlockTagProvider::new);
		pack.addProvider(TesselatingRecipeProvider::new);
	}
}
