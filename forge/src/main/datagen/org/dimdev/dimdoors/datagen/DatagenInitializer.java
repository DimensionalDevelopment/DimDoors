package org.dimdev.dimdoors.datagen;

import datagen.org.dimdev.dimdoors.datagen.AdvancementProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dimdev.dimdoors.DimensionalDoors;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = DimensionalDoors.MOD_ID)
public class DatagenInitializer {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();

		FabricDataGenerator.Pack pack = dataGenerator.createPack();

		generator.addProvider(true, BlockStateProvider::new);
		generator.addProvider(true, DimdoorsRecipeProvider::new);
		generator.addProvider(true, (DataProvider.Factory<DataProvider>) arg -> new AdvancementProvider(arg, event.getLookupProvider()));
		generator.addProvider(true, (DataProvider.Factory<DataProvider>) LootTableProvider::new);
		generator.addProvider(true, (DataProvider.Factory<DataProvider>) LimboDecayProvider::new);
		generator.addProvider(true, (DataProvider.Factory<DataProvider>) arg -> new BlockTagProvider(arg, event.getLookupProvider(), event.getModContainer().getModId(), event.getExistingFileHelper()));
	}
}
