package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class DimDoorsDynamicRegistryDatagen extends DimDoorsDynamicRegistryProvider {
    public DimDoorsDynamicRegistryDatagen(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }


    @Override
    public String getName() {
        return "Dimdoors: Dynamic Registries";
    }

    @Override
    protected void configure(RegistrationHelper context) {

        DefaultDynamicRegistryDataGen.bootstrapCarvers(context);
        DefaultDynamicRegistryDataGen.bootstrapBiomes(context);
        DefaultDynamicRegistryDataGen.bootstrapConfiguredFeature(context);
        DefaultDynamicRegistryDataGen.bootstrapPlacedFeature(context);
        DefaultDynamicRegistryDataGen.bootstrapDimensionType(context);
        DefaultDynamicRegistryDataGen.bootstrapLevelStem(context);
        ModDensityFunctions.bootstrap(context);
        ModNoiseParameters.bootstrap(context);
        ModChunkGeneratorSettings.bootstrap(context);
        DefaultDynamicRegistryDataGen.bootstrapProcessorLists(context);
        DefaultDynamicRegistryDataGen.bootstrapStructures(context);
        DefaultDynamicRegistryDataGen.bootstrapGatewayPools(context);
        DefaultDynamicRegistryDataGen.bootstrapStructureSets(context);
        DefaultDynamicRegistryDataGen.bootstrapJukeboxSongs(context);
        DefaultDynamicRegistryDataGen.bootstrapEnchants(context);
        DefaultDynamicRegistryDataGen.bootstrapPaintings(context);
        DoorDataDataGen.bootstrap(context);
    }
}
