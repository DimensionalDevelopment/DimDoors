package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.dimdev.dimdoors.DimensionalDoors;
import org.jetbrains.annotations.Nullable;

public class DatagenInitializer implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		generator.addProvider(DimDoorsModelProvider::new);
		generator.addProvider(DimdoorsRecipeProvider::new);
		generator.addProvider(AdvancementProvider::new);
		generator.addProvider(LootTableProvider::new);
		generator.addProvider(LimboDecayProvider::new);
		generator.addProvider(BlockTagProvider::new);
		generator.addProvider(ItemTagProvider::new);
		generator.addProvider(BiomeTagProvider::new);
	}

	@Override
	public @Nullable String getEffectiveModId() {
		return DimensionalDoors.MOD_ID;
	}
}
