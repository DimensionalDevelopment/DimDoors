package datagen.org.dimdev.dimdoors.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dimdev.dimdoors.DimensionalDoors;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = DimensionalDoors.MOD_ID)
public class DatagenInitializer {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();

//		generator.addProvider(true, org.dimdev.dimdoors.datagen.BlockStateProvider::new);
		generator.addProvider(true, (DataProvider.Factory<DataProvider>) DimdoorsRecipeProvider::new);
		generator.addProvider(true, (DataProvider.Factory<DataProvider>) arg -> new AdvancementProvider(arg, event.getLookupProvider()));
		generator.addProvider(true, (DataProvider.Factory<DataProvider>) org.dimdev.dimdoors.datagen.LootTableProvider::new);
		generator.addProvider(true, (DataProvider.Factory<DataProvider>) org.dimdev.dimdoors.datagen.LimboDecayProvider::new);
		generator.addProvider(true, (DataProvider.Factory<DataProvider>) arg -> new org.dimdev.dimdoors.datagen.BlockTagProvider(arg, event.getLookupProvider(), event.getModContainer().getModId(), event.getExistingFileHelper()));
	}
}
