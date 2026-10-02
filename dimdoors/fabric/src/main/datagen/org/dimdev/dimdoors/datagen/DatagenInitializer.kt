package org.dimdev.dimdoors.datagen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.world.ModDimensions

class DatagenInitializer : DataGeneratorEntrypoint {

    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {

        val pack = generator.createPack()

//        var defaultPack = generator.createBuiltinResourcePack()

//        pack.addProvider(DefaultPaintingDataGenerator::new);

        pack.addProvider { output, registries -> DimDoorsDynamicRegistryDatagen(output, registries) }

        pack.addProvider { output, registries -> DimDoorsModelProvider(output) }
        pack.addProvider { output, registries -> DimdoorsRecipeProvider(output, registries) }
        pack.addProvider { output, registries -> AdvancementProvider(output, registries) }
        pack.addProvider { output, registries -> BlockLootTableProvider(output, registries) }
        pack.addProvider { output, registries -> ChestLootTableProvider(output, registries) }
        pack.addProvider { output, registries -> BlockUseLootTableProvider(output, registries) }
        pack.addProvider { output, registries -> AbstractionDecayProvider(output, registries) }
        pack.addProvider { output, registries -> LanguageProvider(output, registries) }

        pack.addProvider { output, registries -> BlockTagProvider(output, registries) }
        pack.addProvider { output, registries -> FluidTagProvider(output, registries) }
        pack.addProvider { output, registries -> BiomeTagProvider(output, registries) }
        pack.addProvider { output, registries -> ItemTagProvider(output, registries) }
        pack.addProvider { output, registries -> EnchantmentTagProvider(output, registries) }
        pack.addProvider { output, registries -> PaintingTagProvider(output, registries) }
        pack.addProvider { output, registries -> LevelTagProvider(output, registries) }
        pack.addProvider { output, registries -> EntityTagProvider(output, registries) }

//        pack.addProvider(PocketDataGenClassic::new);
    }

    override fun buildRegistry(registryBuilder: RegistrySetBuilder) {
        registryBuilder.add(Registries.DIMENSION_TYPE, ModDimensions::bootstrap)
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

    override fun getEffectiveModId(): String = DimensionalDoors.MOD_ID
}
