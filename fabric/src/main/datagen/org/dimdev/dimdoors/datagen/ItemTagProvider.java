package org.dimdev.dimdoors.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalFluidTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.tag.ModItemTags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.tag(ModItemTags.DRIFTWOOD_LOGS).add(ModBlocks.DRIFTWOOD_LOG.get().asItem().builtInRegistryHolder().key(), ModBlocks.DRIFTWOOD_WOOD.get().asItem().builtInRegistryHolder().key());

        add(ModItems.RIFT_BLADE, ConventionalItemTags.MELEE_WEAPON_TOOLS,
                ItemTags.FIRE_ASPECT_ENCHANTABLE,
                ConventionalItemTags.ENCHANTABLES,
                ConventionalItemTags.TOOLS,
                ItemTags.BREAKS_DECORATED_POTS,
                ItemTags.VANISHING_ENCHANTABLE,
                ItemTags.DURABILITY_ENCHANTABLE,
                ItemTags.SWORD_ENCHANTABLE,
                ItemTags.WEAPON_ENCHANTABLE,
                ItemTags.SHARP_WEAPON_ENCHANTABLE,
                ItemTags.SWORDS);

        addHead(ModItems.WORLD_THREAD_BOOTS.getKey(), ModItems.GARMENT_OF_REALITY_CHESTPLATE.getKey());
        addChest(ModItems.WORLD_THREAD_CHESTPLATE.getKey(), ModItems.GARMENT_OF_REALITY_BOOTS.getKey());
        addLegs(ModItems.WORLD_THREAD_HELMET.getKey(), ModItems.GARMENT_OF_REALITY_HELMET.getKey());
        addFeet(ModItems.WORLD_THREAD_LEGGINGS.getKey(), ModItems.GARMENT_OF_REALITY_LEGGINGS.getKey());

        tag(ConventionalItemTags.MUSIC_DISCS).add(
                ModItems.THEY_STARE_BACK_RECORD.getKey(),
                ModItems.WHITE_VOID_RECORD.getKey(),
                ModItems.CREEPY_RECORD.getKey()
        );

        tag(ConventionalItemTags.BUCKETS).add(ModItems.LEAK_BUCKET.getKey(), ModItems.ETERNAL_FLUID_BUCKET.getKey());

        add(ItemTags.DOORS,
                ModBlocks.GOLD_DOOR,
                ModBlocks.AMALGAM_DOOR,
                ModBlocks.STONE_DOOR
        );

        add(ModItems.CLOD, ItemTags.BEACON_PAYMENT_ITEMS, ConventionalItemTags.GEMS);
        add(ModBlocks.CLOD_ORE, ConventionalItemTags.ORES);
        add(ModBlocks.CLOD_BLOCK,
                ConventionalItemTags.STORAGE_BLOCKS
        );

        add(ModItems.AMALGAM_LUMP,
                ItemTags.BEACON_PAYMENT_ITEMS,
                ConventionalItemTags.INGOTS
        );
        add(ModBlocks.AMALGAM_BLOCK, ConventionalItemTags.STORAGE_BLOCKS);
        add(ModBlocks.AMALGAM_TRAPDOOR, ItemTags.TRAPDOORS);
        add(ModBlocks.AMALGAM_SLAB, ItemTags.SLABS);
        add(ModBlocks.AMALGAM_STAIRS, ItemTags.STAIRS);
        add(ModBlocks.AMALGAM_ORE);

        add(ModBlocks.DRIFTWOOD_WOOD,
                ItemTags.COMPLETES_FIND_TREE_TUTORIAL,
                ItemTags.LOGS,
                ItemTags.LOGS_THAT_BURN,
                ModItemTags.DRIFTWOOD_LOGS
        );
        add(ModBlocks.DRIFTWOOD_LOG,
                ItemTags.COMPLETES_FIND_TREE_TUTORIAL,
                ItemTags.LOGS,
                ItemTags.LOGS_THAT_BURN,
                ModItemTags.DRIFTWOOD_LOGS
        );

        add(ModBlocks.DRIFTWOOD_PLANKS,
                ItemTags.PLANKS
        );
        add(ModBlocks.DRIFTWOOD_LEAVES,
                ItemTags.LEAVES,
                ItemTags.COMPLETES_FIND_TREE_TUTORIAL
        );

        add(ModBlocks.DRIFTWOOD_SAPLING, ItemTags.SAPLINGS);
        add(ModBlocks.DRIFTWOOD_FENCE, ItemTags.FENCES, ItemTags.WOODEN_FENCES, ConventionalItemTags.FENCES, ConventionalItemTags.WOODEN_FENCES);
        add(ModBlocks.DRIFTWOOD_GATE, ItemTags.FENCE_GATES, ConventionalItemTags.FENCE_GATES, ConventionalItemTags.WOODEN_FENCE_GATES);
        add(ModBlocks.DRIFTWOOD_BUTTON, ItemTags.WOODEN_BUTTONS, ItemTags.BUTTONS);
        add(ModBlocks.DRIFTWOOD_SLAB, ItemTags.SLABS, ItemTags.WOODEN_SLABS);
        add(ModBlocks.DRIFTWOOD_STAIRS, ItemTags.STAIRS, ItemTags.WOODEN_STAIRS);
        add(ModBlocks.DRIFTWOOD_DOOR,
                ItemTags.DOORS, ItemTags.WOODEN_DOORS
        );
        add(ModBlocks.DRIFTWOOD_TRAPDOOR, ItemTags.WOODEN_TRAPDOORS, ItemTags.TRAPDOORS);

        ModBlocks.DecayGroupSet.SETS.forEach(this::add);
    }

    private void add(TagKey<Item> tag, RegistrySupplier<? extends ItemLike>... suppliers) {
        tag(tag).addAll(Stream.of(suppliers)
                .map(Supplier::get)
                .map(ItemLike::asItem)
                .map(Item::builtInRegistryHolder)
                .map(Holder.Reference::key)
                .toList());
    }

    private void add(ModBlocks.DecayGroupSet set) {
        add(set.button(), ItemTags.BUTTONS);
        add(set.gate(), ItemTags.FENCE_GATES, ConventionalItemTags.FENCE_GATES);
        add(set.fence(), ItemTags.FENCES, ConventionalItemTags.FENCES);
        add(set.wall(), ItemTags.WALLS);
        add(set.slab(), ItemTags.SLABS);
        add(set.stairs(), ItemTags.STAIRS);
    }

    private void addHead(ResourceKey<Item>... items) {
        addDefaultArmor(items);
        tag(ItemTags.HEAD_ARMOR).add(items);
        tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(items);
    }

    private void addChest(ResourceKey<Item>... items) {
        addDefaultArmor(items);
        tag(ItemTags.CHEST_ARMOR).add(items);
        tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(items);
    }

    private void addLegs(ResourceKey<Item>... items) {
        addDefaultArmor(items);
        tag(ItemTags.LEG_ARMOR).add(items);
        tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(items);
    }

    private void addFeet(ResourceKey<Item>... items) {
        addDefaultArmor(items);
        tag(ItemTags.FOOT_ARMOR).add(items);
        tag(ItemTags.FOOT_ARMOR_ENCHANTABLE).add(items);
    }

    private void addDefaultArmor(ResourceKey<Item>[] items) {
        tag(ConventionalItemTags.ARMORS).add(items);
        tag(ItemTags.TRIMMABLE_ARMOR).add(items);
        tag(ItemTags.ARMOR_ENCHANTABLE).add(items);
        tag(ItemTags.EQUIPPABLE_ENCHANTABLE).add(items);
        tag(ItemTags.VANISHING_ENCHANTABLE).add(items);
        tag(ItemTags.DURABILITY_ENCHANTABLE).add(items);
        tag(ModItemTags.LIMBO_GAZE_DEFYING).add(items);
    }

    private void add(RegistrySupplier<? extends ItemLike> supplier, TagKey<Item>... tags) {
        for(var tag : tags) this.tag(tag).add(supplier.get().asItem().builtInRegistryHolder().key());
    }
}
