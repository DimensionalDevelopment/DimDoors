package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.tag.ModItemTags;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class ItemTagProvider extends DimDoorsTagsProvider<Item> {
    public ItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, Registries.ITEM, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.tag(ModItemTags.DRIFTWOOD_LOGS).add(ModBlocks.DRIFTWOOD_LOG.asItem().builtInRegistryHolder().key(), ModBlocks.DRIFTWOOD_WOOD.asItem().builtInRegistryHolder().key());
        this.tag(ModItemTags.TRANSCENDENT_ENCHANTABLE)
                .addTag(ItemTags.BOW_ENCHANTABLE)
                .addTag(ItemTags.CROSSBOW_ENCHANTABLE);

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

        addHead(ModItems.WORLD_THREAD_ARMOR.helmet().builtInRegistryHolder().key(), ModItems.GARMENT_OF_REALITY_ARMOR.helmet().builtInRegistryHolder().key());
        addChest(ModItems.WORLD_THREAD_ARMOR.chestplate().builtInRegistryHolder().key(), ModItems.GARMENT_OF_REALITY_ARMOR.chestplate().builtInRegistryHolder().key());
        addLegs(ModItems.WORLD_THREAD_ARMOR.leggings().builtInRegistryHolder().key(), ModItems.GARMENT_OF_REALITY_ARMOR.leggings().builtInRegistryHolder().key());
        addFeet(ModItems.WORLD_THREAD_ARMOR.boots().builtInRegistryHolder().key(), ModItems.GARMENT_OF_REALITY_ARMOR.boots().builtInRegistryHolder().key());

        tag(ConventionalItemTags.MUSIC_DISCS).add(
                ModItems.THEY_STARE_BACK_RECORD.builtInRegistryHolder().key(),
                ModItems.WHITE_VOID_RECORD.builtInRegistryHolder().key(),
                ModItems.CREEPY_RECORD.builtInRegistryHolder().key()
        );

        tag(ConventionalItemTags.BUCKETS).add(ModItems.LEAK_BUCKET.builtInRegistryHolder().key(), ModItems.ETERNAL_FLUID_BUCKET.builtInRegistryHolder().key());

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

        add(ModBlocks.WHITE_FABRIC, ConventionalItemTags.WHITE_DYED, ModItemTags.FABRIC);
        add(ModBlocks.ORANGE_FABRIC, ConventionalItemTags.ORANGE_DYED, ModItemTags.FABRIC);
        add(ModBlocks.MAGENTA_FABRIC, ConventionalItemTags.MAGENTA_DYED, ModItemTags.FABRIC);
        add(ModBlocks.LIGHT_BLUE_FABRIC, ConventionalItemTags.LIGHT_BLUE_DYED, ModItemTags.FABRIC);
        add(ModBlocks.YELLOW_FABRIC, ConventionalItemTags.YELLOW_DYED, ModItemTags.FABRIC);
        add(ModBlocks.LIME_FABRIC, ConventionalItemTags.LIME_DYED, ModItemTags.FABRIC);
        add(ModBlocks.PINK_FABRIC, ConventionalItemTags.PINK_DYED, ModItemTags.FABRIC);
        add(ModBlocks.GRAY_FABRIC, ConventionalItemTags.GRAY_DYED, ModItemTags.FABRIC);
        add(ModBlocks.LIGHT_GRAY_FABRIC, ConventionalItemTags.LIGHT_GRAY_DYED, ModItemTags.FABRIC);
        add(ModBlocks.CYAN_FABRIC, ConventionalItemTags.CYAN_DYED, ModItemTags.FABRIC);
        add(ModBlocks.PURPLE_FABRIC, ConventionalItemTags.PURPLE_DYED, ModItemTags.FABRIC);
        add(ModBlocks.BLUE_FABRIC, ConventionalItemTags.BLUE_DYED, ModItemTags.FABRIC);
        add(ModBlocks.BROWN_FABRIC, ConventionalItemTags.BROWN_DYED, ModItemTags.FABRIC);
        add(ModBlocks.GREEN_FABRIC, ConventionalItemTags.GREEN_DYED, ModItemTags.FABRIC);
        add(ModBlocks.RED_FABRIC, ConventionalItemTags.RED_DYED, ModItemTags.FABRIC);
        add(ModBlocks.BLACK_FABRIC, ConventionalItemTags.BLACK_DYED, ModItemTags.FABRIC);

        add(ModBlocks.WHITE_ANCIENT_FABRIC, ConventionalItemTags.WHITE_DYED, ModItemTags.ANCIENT_FABRIC);
        add(ModBlocks.ORANGE_ANCIENT_FABRIC, ConventionalItemTags.ORANGE_DYED, ModItemTags.ANCIENT_FABRIC);
        add(ModBlocks.MAGENTA_ANCIENT_FABRIC, ConventionalItemTags.MAGENTA_DYED, ModItemTags.ANCIENT_FABRIC);
        add(ModBlocks.LIGHT_BLUE_ANCIENT_FABRIC, ConventionalItemTags.LIGHT_BLUE_DYED, ModItemTags.ANCIENT_FABRIC);
        add(ModBlocks.YELLOW_ANCIENT_FABRIC, ConventionalItemTags.YELLOW_DYED, ModItemTags.ANCIENT_FABRIC);
        add(ModBlocks.LIME_ANCIENT_FABRIC, ConventionalItemTags.LIME_DYED, ModItemTags.ANCIENT_FABRIC);
        add(ModBlocks.PINK_ANCIENT_FABRIC, ConventionalItemTags.PINK_DYED, ModItemTags.ANCIENT_FABRIC);
        add(ModBlocks.GRAY_ANCIENT_FABRIC, ConventionalItemTags.GRAY_DYED, ModItemTags.ANCIENT_FABRIC);
        add(ModBlocks.LIGHT_GRAY_ANCIENT_FABRIC, ConventionalItemTags.LIGHT_GRAY_DYED, ModItemTags.ANCIENT_FABRIC);
        add(ModBlocks.CYAN_ANCIENT_FABRIC, ConventionalItemTags.CYAN_DYED, ModItemTags.ANCIENT_FABRIC);
        add(ModBlocks.PURPLE_ANCIENT_FABRIC, ConventionalItemTags.PURPLE_DYED, ModItemTags.ANCIENT_FABRIC);
        add(ModBlocks.BLUE_ANCIENT_FABRIC, ConventionalItemTags.BLUE_DYED, ModItemTags.ANCIENT_FABRIC);
        add(ModBlocks.BROWN_ANCIENT_FABRIC, ConventionalItemTags.BROWN_DYED, ModItemTags.ANCIENT_FABRIC);
        add(ModBlocks.GREEN_ANCIENT_FABRIC, ConventionalItemTags.GREEN_DYED, ModItemTags.ANCIENT_FABRIC);
        add(ModBlocks.RED_ANCIENT_FABRIC, ConventionalItemTags.RED_DYED, ModItemTags.ANCIENT_FABRIC);
        add(ModBlocks.BLACK_ANCIENT_FABRIC, ConventionalItemTags.BLACK_DYED, ModItemTags.ANCIENT_FABRIC);
    }

    private void add(TagKey<Item> tag, ItemLike... suppliers) {
        tag(tag).addAll(Stream.of(suppliers)
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

    @SafeVarargs
    private void addHead(ResourceKey<Item>... items) {
        addDefaultArmor(items);
        tag(ItemTags.HEAD_ARMOR).add(items);
        tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(items);
    }

    @SafeVarargs
    private void addChest(ResourceKey<Item>... items) {
        addDefaultArmor(items);
        tag(ItemTags.CHEST_ARMOR).add(items);
        tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(items);
    }

    @SafeVarargs
    private void addLegs(ResourceKey<Item>... items) {
        addDefaultArmor(items);
        tag(ItemTags.LEG_ARMOR).add(items);
        tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(items);
    }

    @SafeVarargs
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

    @SafeVarargs
    private void add(ItemLike supplier, TagKey<Item>... tags) {
        for(var tag : tags) this.tag(tag).add(supplier.asItem().builtInRegistryHolder().key());
    }
}
