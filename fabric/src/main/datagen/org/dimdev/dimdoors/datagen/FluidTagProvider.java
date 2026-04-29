package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalFluidTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;
import org.dimdev.dimdoors.fluid.ModFluids;

import java.util.concurrent.CompletableFuture;

public class FluidTagProvider extends FabricTagProvider.FluidTagProvider {
    public FluidTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(FluidTags.WATER).add(
                ModFluids.LEAK.builtInRegistryHolder().key(),
                ModFluids.FLOWING_LEAK.builtInRegistryHolder().key()
        );
    }
}
