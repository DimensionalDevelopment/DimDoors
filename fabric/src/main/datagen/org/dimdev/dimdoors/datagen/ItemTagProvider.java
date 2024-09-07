package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.tag.ModItemTags;

public class ItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ItemTagProvider(FabricDataGenerator output) {
        super(output);
    }

    @Override
    protected void generateTags() {
        this.tag(ItemTags.MUSIC_DISCS).add(ModItems.CREEPY_RECORD.getOrNull().builtInRegistryHolder().key(), ModItems.WHITE_VOID_RECORD.getOrNull().builtInRegistryHolder().key());

        this.tag(ModItemTags.DIAMONDS)/*.add(Items.DIAMOND.builtInRegistryHolder().key())*/.addOptionalTag(new ResourceLocation("c", "diamonds")).addOptionalTag(new ResourceLocation("forge", "gems/diamond"));
        this.tag(ModItemTags.GOLD_INGOTS)/*.add(Items.IRON_INGOT.builtInRegistryHolder().key())*/.addOptionalTag(new ResourceLocation("c", "gold_ingots")).addOptionalTag(new ResourceLocation("forge", "ingots/gold"));
        this.tag(ModItemTags.IRON_INGOTS)/*.add(Items.DIAMOND.builtInRegistryHolder().key())*/.addOptionalTag(new ResourceLocation("c", "iron_ingots")).addOptionalTag(new ResourceLocation("forge", "ingots/iron"));

        this.tag(ModItemTags.LIMBO_GAZE_DEFYING).add(ModItems.WORLD_THREAD_BOOTS.get(), ModItems.WORLD_THREAD_CHESTPLATE.get(), ModItems.WORLD_THREAD_HELMET.get(), ModItems.WORLD_THREAD_LEGGINGS.get(), ModItems.GARMENT_OF_REALITY_CHESTPLATE.get(), ModItems.GARMENT_OF_REALITY_BOOTS.get(), ModItems.GARMENT_OF_REALITY_HELMET.get(), ModItems.GARMENT_OF_REALITY_LEGGINGS.get());

    }
}
