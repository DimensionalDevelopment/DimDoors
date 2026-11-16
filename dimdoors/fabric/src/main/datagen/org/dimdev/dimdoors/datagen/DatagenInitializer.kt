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

        pack.addProvider(::DimDoorsDynamicRegistryDatagen)

        pack.addProvider { output, _ -> DimDoorsModelProvider(output) }
        pack.addProvider(::DimdoorsRecipeProvider)
        pack.addProvider(::AdvancementProvider)
        pack.addProvider(::BlockLootTableProvider)
        pack.addProvider(::ChestLootTableProvider)
        pack.addProvider(::BlockUseLootTableProvider)
        pack.addProvider(::AbstractionDecayProvider)
        pack.addProvider(::LanguageProvider)

        pack.addProvider(::BlockTagProvider)
        pack.addProvider(::FluidTagProvider)
        pack.addProvider(::BiomeTagProvider)
        pack.addProvider(::ItemTagProvider)
        pack.addProvider(::EnchantmentTagProvider)
        pack.addProvider(::PaintingTagProvider)
        pack.addProvider(::LevelTagProvider)
        pack.addProvider(::EntityTagProvider)

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
