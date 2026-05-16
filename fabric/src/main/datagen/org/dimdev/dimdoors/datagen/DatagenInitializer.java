package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.ModRegistryKeys;
import org.dimdev.dimdoors.enchantment.ModEnchants;
import org.dimdev.dimdoors.item.ModJukeboxSongs;
import org.dimdev.dimdoors.painting.ModPaintings;
import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.ModGatewayPools;
import org.dimdev.dimdoors.world.ModProcessorLists;
import org.dimdev.dimdoors.world.ModStructures;
import org.dimdev.dimdoors.world.carvers.ModCarvers;
import org.jetbrains.annotations.Nullable;

public class DatagenInitializer implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {

        var pack = generator.createPack();

//        var defaultPack = generator.createBuiltinResourcePack()

//        pack.addProvider(DefaultPaintingDataGenerator::new);

        pack.addProvider(DimDoorsDynamicRegistryDatagen::new);

        pack.addProvider(DimDoorsModelProvider::new);
        pack.addProvider(DimdoorsRecipeProvider::new);
        pack.addProvider(AdvancementProvider::new);
        pack.addProvider(BlockLootTableProvider::new);
        pack.addProvider(ChestLootTableProvider::new);
        pack.addProvider(BlockUseLootTableProvider::new);
        pack.addProvider(AbstractionDecayProvider::new);
        pack.addProvider(LanguageProvider::new);

        pack.addProvider(BlockTagProvider::new);
        pack.addProvider(FluidTagProvider::new);
        pack.addProvider(BiomeTagProvider::new);
        pack.addProvider(ItemTagProvider::new);
        pack.addProvider(PaintingTagProvider::new);

//        pack.addProvider(PocketDataGenClassic::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
//        registryBuilder.add(Registries.BIOME, ModBiomes::bootstrap)
//                .add(Registries.CONFIGURED_FEATURE, DefaultDynamicRegistryDataGen::bootstrapConfiguredFeature)
//                .add(Registries.PLACED_FEATURE, DefaultDynamicRegistryDataGen::bootstrapPlacedFeature)
//                .add(Registries.DIMENSION_TYPE, DefaultDynamicRegistryDataGen::bootstrapDimensionType)
//                .add(Registries.LEVEL_STEM, DefaultDynamicRegistryDataGen::bootstrapLevelStem)
//                .add(Registries.DENSITY_FUNCTION, ModDensityFunctions::bootstrap)
//                .add(Registries.NOISE, ModNoiseParameters::bootstrap)
//                .add(Registries.NOISE_SETTINGS, ModChunkGeneratorSettings::bootstrap)
//                .add(Registries.CONFIGURED_CARVER, ModCarvers::bootstrap)
//                .add(Registries.STRUCTURE, ModStructures::new)
//                .add(Registries.TEMPLATE_POOL, ModGatewayPools::bootstrap)
//                .add(Registries.STRUCTURE_SET, ModStructureSets::bootstrap)
//                .add(Registries.PROCESSOR_LIST, ModProcessorLists::bootstrap)
//                .add(Registries.JUKEBOX_SONG, ModJukeboxSongs::bootstrap)
//                .add(Registries.ENCHANTMENT, ModEnchants::bootstrap)
//                .add(Registries.PAINTING_VARIANT, ModPaintings::bootstrap)
//                .add(ModRegistryKeys.RIFT_DATA, DoorDataDataGen::bootstrap)

    }

    @Override
    public @Nullable String getEffectiveModId() {
        return DimensionalDoors.MOD_ID;
    }
}
