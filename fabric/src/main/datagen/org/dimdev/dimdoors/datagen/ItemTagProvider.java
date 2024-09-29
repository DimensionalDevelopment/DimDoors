package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.tag.ModItemTags;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.tag(ModItemTags.DIAMONDS)/*.add(Items.DIAMOND.builtInRegistryHolder().key())*/.addOptionalTag(ResourceLocation.tryBuild("c", "diamonds")).addOptionalTag(ResourceLocation.tryBuild("forge", "gems/diamond"));
        this.tag(ModItemTags.GOLD_INGOTS)/*.add(Items.IRON_INGOT.builtInRegistryHolder().key())*/.addOptionalTag(ResourceLocation.tryBuild("c", "gold_ingots")).addOptionalTag(ResourceLocation.tryBuild("forge", "ingots/gold"));
        this.tag(ModItemTags.IRON_INGOTS)/*.add(Items.DIAMOND.builtInRegistryHolder().key())*/.addOptionalTag(ResourceLocation.tryBuild("c", "iron_ingots")).addOptionalTag(ResourceLocation.tryBuild("forge", "ingots/iron"));

        this.tag(ModItemTags.LIMBO_GAZE_DEFYING).add(ModItems.WORLD_THREAD_BOOTS.getKey(), ModItems.WORLD_THREAD_CHESTPLATE.getKey(), ModItems.WORLD_THREAD_HELMET.getKey(), ModItems.WORLD_THREAD_LEGGINGS.getKey(), ModItems.GARMENT_OF_REALITY_CHESTPLATE.getKey(), ModItems.GARMENT_OF_REALITY_BOOTS.getKey(), ModItems.GARMENT_OF_REALITY_HELMET.getKey(), ModItems.GARMENT_OF_REALITY_LEGGINGS.getKey());
    }
}
