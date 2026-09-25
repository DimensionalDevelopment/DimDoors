package org.dimdev.dimdoors.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.core.HolderLookup
import org.dimdev.dimdoors.datagen.DefaultDynamicRegistryDataGen.bootstrapBiomes
import org.dimdev.dimdoors.datagen.DefaultDynamicRegistryDataGen.bootstrapCarvers
import org.dimdev.dimdoors.datagen.DefaultDynamicRegistryDataGen.bootstrapConfiguredFeature
import org.dimdev.dimdoors.datagen.DefaultDynamicRegistryDataGen.bootstrapDimensionType
import org.dimdev.dimdoors.datagen.DefaultDynamicRegistryDataGen.bootstrapEnchants
import org.dimdev.dimdoors.datagen.DefaultDynamicRegistryDataGen.bootstrapGatewayPools
import org.dimdev.dimdoors.datagen.DefaultDynamicRegistryDataGen.bootstrapJukeboxSongs
import org.dimdev.dimdoors.datagen.DefaultDynamicRegistryDataGen.bootstrapLevelStem
import org.dimdev.dimdoors.datagen.DefaultDynamicRegistryDataGen.bootstrapPaintings
import org.dimdev.dimdoors.datagen.DefaultDynamicRegistryDataGen.bootstrapPlacedFeature
import org.dimdev.dimdoors.datagen.DefaultDynamicRegistryDataGen.bootstrapProcessorLists
import org.dimdev.dimdoors.datagen.DefaultDynamicRegistryDataGen.bootstrapStructureSets
import org.dimdev.dimdoors.datagen.DefaultDynamicRegistryDataGen.bootstrapStructures
import java.util.concurrent.CompletableFuture

class DimDoorsDynamicRegistryDatagen(output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) : DimDoorsDynamicRegistryProvider(output, registriesFuture) {
    override fun getName(): String {
        return "Dimdoors: Dynamic Registries"
    }

    override fun configure(context: RegistrationHelper) {
        bootstrapCarvers(context)
        bootstrapBiomes(context)
        bootstrapConfiguredFeature(context)
        bootstrapPlacedFeature(context)
        bootstrapDimensionType(context)
        bootstrapLevelStem(context)
        ModDensityFunctions.bootstrap(context)
        ModNoiseParameters.bootstrap(context)
        ModChunkGeneratorSettings.bootstrap(context)
        bootstrapProcessorLists(context)
        bootstrapStructures(context)
        bootstrapGatewayPools(context)
        bootstrapStructureSets(context)
        bootstrapJukeboxSongs(context)
        bootstrapEnchants(context)
        bootstrapPaintings(context)
        DoorDataDataGen.bootstrap(context)
    }
}
