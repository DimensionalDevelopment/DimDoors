package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.DataProvider;
import org.dimdev.dimdoors.DimensionalDoors;
import org.jetbrains.annotations.Nullable;

public class DatagenInitializer implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		var pack = generator.createPack();

		pack.addProvider(DimDoorsModelProvider::new);
		pack.addProvider((DataProvider.Factory<DataProvider>) DimdoorsRecipeProvider::new);
		pack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<DataProvider>) AdvancementProvider::new);
		pack.addProvider(org.dimdev.dimdoors.datagen.LootTableProvider::new);
		pack.addProvider((DataProvider.Factory<DataProvider>) org.dimdev.dimdoors.datagen.LimboDecayProvider::new);
		pack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<DataProvider>) BlockTagProvider::new);
		pack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<DataProvider>) ItemTagProvider::new);
	}

	@Override
	public @Nullable String getEffectiveModId() {
		return DimensionalDoors.MOD_ID;
	}
}
