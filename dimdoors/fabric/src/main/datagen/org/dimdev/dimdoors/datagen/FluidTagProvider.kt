package org.dimdev.dimdoors.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.tags.FluidTags
import org.dimdev.dimdoors.fluid.ModFluids
import java.util.concurrent.CompletableFuture

class FluidTagProvider(output: FabricDataOutput, completableFuture: CompletableFuture<HolderLookup.Provider>) : FabricTagProvider.FluidTagProvider(output, completableFuture) {
    override fun addTags(provider: HolderLookup.Provider) {
        tag(FluidTags.WATER).add(
            reverseLookup(ModFluids.LEAK),
            reverseLookup(ModFluids.FLOWING_LEAK)
        )
    }
}
