package org.dimdev.dimdoors.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.core.HolderLookup
import org.dimdev.dimdoors.ModRegistryKeys
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
import org.dimdev.dimdoors.item.door.RiftDataList
import java.util.concurrent.CompletableFuture

class DimDoorsDynamicRegistryDatagen(output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) : DimDoorsDynamicRegistryProvider(output, registriesFuture) {
    override fun getName(): String {
        return "Dimdoors: Dynamic Registries"
    }

    override fun registerAdditional(entries: Entries) {
        entries.addRegistry(ModRegistryKeys.DOOR_DATA, RiftDataList.CODEC)
    }
    override fun configure(helper: RegistrationHelper) {
        bootstrapCarvers(helper)
        bootstrapBiomes(helper)
        bootstrapConfiguredFeature(helper)
        bootstrapPlacedFeature(helper)
        bootstrapDimensionType(helper)
        bootstrapLevelStem(helper)
        ModDensityFunctions.bootstrap(helper)
        ModNoiseParameters.bootstrap(helper)
        ModChunkGeneratorSettings.bootstrap(helper)
        bootstrapProcessorLists(helper)
        bootstrapStructures(helper)
        bootstrapGatewayPools(helper)
        bootstrapStructureSets(helper)
        bootstrapJukeboxSongs(helper)
        bootstrapEnchants(helper)
        bootstrapPaintings(helper)
        DoorDataDataGen.bootstrap(helper)
    }
}
