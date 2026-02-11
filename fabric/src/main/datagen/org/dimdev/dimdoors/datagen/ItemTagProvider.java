package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.tag.ModItemTags;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.tag(ModItemTags.DRIFTWOOD_LOGS).add(ModBlocks.DRIFTWOOD_LOG.get().asItem().builtInRegistryHolder().key(), ModBlocks.DRIFTWOOD_WOOD.get().asItem().builtInRegistryHolder().key());
        this.tag(ModItemTags.LIMBO_GAZE_DEFYING).add(ModItems.WORLD_THREAD_BOOTS.getKey(), ModItems.WORLD_THREAD_CHESTPLATE.getKey(), ModItems.WORLD_THREAD_HELMET.getKey(), ModItems.WORLD_THREAD_LEGGINGS.getKey(), ModItems.GARMENT_OF_REALITY_CHESTPLATE.getKey(), ModItems.GARMENT_OF_REALITY_BOOTS.getKey(), ModItems.GARMENT_OF_REALITY_HELMET.getKey(), ModItems.GARMENT_OF_REALITY_LEGGINGS.getKey());
    }
}
