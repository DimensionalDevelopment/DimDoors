package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.tag.ModBlockTags;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public BlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        configure(arg.asGetterLookup());
    }

    protected void configure(HolderGetter.Provider arg) {
        add(ModBlockTags.DECAYS_TO_MUD,
                Blocks.BONE_BLOCK,
                Blocks.CACTUS,
                Blocks.CAKE,
                Blocks.CHEST,
                Blocks.COAL_BLOCK,
                Blocks.COCOA,
                Blocks.COMPOSTER,
                Blocks.DIRT,
                Blocks.DRIED_KELP_BLOCK,
                ModBlocks.DRIFTWOOD_LOG,
                ModBlocks.DRIFTWOOD_PLANKS,
                ModBlocks.DRIFTWOOD_WOOD,
                Blocks.HAY_BLOCK,
                Blocks.HONEYCOMB_BLOCK,
                Blocks.LECTERN,
                Blocks.MELON,
                Blocks.MOSS_BLOCK,
                Blocks.NETHER_WART_BLOCK,
                Blocks.PACKED_MUD,
                Blocks.PISTON,
                Blocks.PUMPKIN,
                Blocks.PURPUR_BLOCK,
                Blocks.SKELETON_SKULL,
                Blocks.SKELETON_WALL_SKULL,
                Blocks.SLIME_BLOCK
        );
        add(ModBlockTags.MINOR_PLANTS,
                Blocks.FERN,
                Blocks.DEAD_BUSH,
                Blocks.HANGING_ROOTS,
                Blocks.SEAGRASS,
                Blocks.TALL_SEAGRASS,
                Blocks.TALL_GRASS,
                Blocks.LARGE_FERN,
                Blocks.NETHER_SPROUTS
        );
        addOptional(ModBlockTags.MINOR_PLANTS, "short_grass");
        add(ModBlockTags.DECAYS_TO_AIR,
                Blocks.COBWEB,
                Blocks.SUGAR_CANE,
                ModBlockTags.MINOR_PLANTS
        );
        add(ModBlockTags.DECAYS_TO_CLAY,
                Blocks.MUD,
                Blocks.BRICKS,
                Blocks.TERRACOTTA
        );
        add(ModBlockTags.DECAYS_TO_GRITTY_STONE,
                Blocks.INFESTED_STONE,
                Blocks.INFESTED_COBBLESTONE,
                Blocks.INFESTED_STONE_BRICKS,
                Blocks.INFESTED_MOSSY_STONE_BRICKS,
                Blocks.INFESTED_CRACKED_STONE_BRICKS,
                Blocks.INFESTED_CHISELED_STONE_BRICKS
        );
        add(ModBlockTags.DECAYS_TO_SOLID_STATIC,
                Blocks.BEDROCK,
                Blocks.END_PORTAL_FRAME,
                Blocks.COMMAND_BLOCK,
                Blocks.CHAIN_COMMAND_BLOCK,
                Blocks.REPEATING_COMMAND_BLOCK
        );
        add(ModBlockTags.DECAYS_TO_LINT_LAYER,
                Blocks.MOSS_CARPET,
                Blocks.RAIL,
                ModBlocks.DARK_SAND_LAYER,
                ModBlocks.AMALGAM_TRAPDOOR,
                ModBlocks.DRIFTWOOD_TRAPDOOR,
                Blocks.SNOW,
                Blocks.REDSTONE_WIRE,
                Blocks.REDSTONE_TORCH,
                Blocks.REDSTONE_WALL_TORCH,
                Blocks.REPEATER,
                Blocks.COMPARATOR
        );
        add(ModBlockTags.DECAYS_TO_DARK_SAND_LAYER,
                Blocks.GLASS_PANE,
                Blocks.AMETHYST_CLUSTER,
                Blocks.END_ROD
        );
        add(ModBlockTags.DECAYS_TO_PALE_SAND,
                Blocks.SNOW_BLOCK,
                Blocks.ICE
        );
        add(ModBlockTags.DECAYS_TO_CLAY_FENCE,
                ModBlocks.MUD_SET.fence(),
                ModBlocks.TERRACOTTA_SET.fence()
        );
        add(ModBlockTags.DECAYS_TO_CLAY_GATE,
                ModBlocks.MUD_SET.gate(),
                ModBlocks.TERRACOTTA_SET.gate()
        );
        add(ModBlockTags.DECAYS_TO_CLAY_BUTTON,
                ModBlocks.MUD_SET.button(),
                ModBlocks.TERRACOTTA_SET.button()
        );
        add(ModBlockTags.DECAYS_TO_CLAY_SLAB,
                ModBlocks.MUD_SET.slab(),
                ModBlocks.TERRACOTTA_SET.slab(),
                Blocks.BRICK_SLAB
        );
        add(ModBlockTags.DECAYS_TO_CLAY_STAIRS,
                ModBlocks.MUD_SET.stairs(),
                ModBlocks.TERRACOTTA_SET.stairs(),
                Blocks.BRICK_STAIRS
        );
        add(ModBlockTags.DECAYS_TO_CLAY_WALL,
                ModBlocks.MUD_SET.wall(),
                ModBlocks.TERRACOTTA_SET.wall(),
                Blocks.BRICK_WALL
        );

        add(ModBlockTags.DECAYS_TO_DARK_SAND,
                Blocks.CLAY,
                Blocks.GLASS,
                Blocks.RED_SAND,
                Blocks.SAND,
                Blocks.SOUL_SAND,
                Blocks.STONE,
                Blocks.WHITE_CONCRETE_POWDER,
                Blocks.ORANGE_CONCRETE_POWDER,
                Blocks.MAGENTA_CONCRETE_POWDER,
                Blocks.LIGHT_BLUE_CONCRETE_POWDER,
                Blocks.YELLOW_CONCRETE_POWDER,
                Blocks.LIME_CONCRETE_POWDER,
                Blocks.PINK_CONCRETE_POWDER,
                Blocks.GRAY_CONCRETE_POWDER,
                Blocks.LIGHT_GRAY_CONCRETE_POWDER,
                Blocks.CYAN_CONCRETE_POWDER,
                Blocks.PURPLE_CONCRETE_POWDER,
                Blocks.BLUE_CONCRETE_POWDER,
                Blocks.BROWN_CONCRETE_POWDER,
                Blocks.GREEN_CONCRETE_POWDER,
                Blocks.RED_CONCRETE_POWDER,
                Blocks.BLACK_CONCRETE_POWDER,
                Blocks.TUBE_CORAL_BLOCK,
                Blocks.BRAIN_CORAL_BLOCK,
                Blocks.BUBBLE_CORAL_BLOCK,
                Blocks.FIRE_CORAL_BLOCK,
                Blocks.HORN_CORAL_BLOCK,
                Blocks.DEAD_TUBE_CORAL_BLOCK,
                Blocks.DEAD_BRAIN_CORAL_BLOCK,
                Blocks.DEAD_BUBBLE_CORAL_BLOCK,
                Blocks.DEAD_FIRE_CORAL_BLOCK,
                Blocks.DEAD_HORN_CORAL_BLOCK
        );
        add(ModBlockTags.DECAYS_TO_DARK_SAND_FENCE, ModBlocks.CLAY_SET.fence());
        add(ModBlockTags.DECAYS_TO_DARK_SAND_GATE, ModBlocks.CLAY_SET.gate());
        add(ModBlockTags.DECAYS_TO_DARK_SAND_BUTTON, ModBlocks.CLAY_SET.button());
        add(ModBlockTags.DECAYS_TO_DARK_SAND_SLAB,
                ModBlocks.CLAY_SET.slab(),
                ModBlocks.STONE_SLAB
        );
        add(ModBlockTags.DECAYS_TO_DARK_SAND_STAIRS,
                ModBlocks.CLAY_SET.stairs(),
                ModBlocks.STONE_STAIRS
        );
        add(ModBlockTags.DECAYS_TO_DARK_SAND_WALL,
                ModBlocks.CLAY_SET.wall(),
                ModBlocks.STONE_WALL
        );

        add(ModBlockTags.DECAYS_TO_UNRAVELED_FABRIC,
                ModBlocks.DARK_SAND,
                ModBlocks.PALE_SAND,
                ModBlocks.LINT_LAYER,
                Blocks.WHITE_WOOL
        );
        add(ModBlockTags.DECAYS_TO_UNRAVELED_FENCE, ModBlocks.DARK_SAND_SET.fence());
        add(ModBlockTags.DECAYS_TO_UNRAVELED_GATE, ModBlocks.DARK_SAND_SET.gate());
        add(ModBlockTags.DECAYS_TO_UNRAVELED_BUTTON, ModBlocks.DARK_SAND_SET.button());
        add(ModBlockTags.DECAYS_TO_UNRAVELED_SLAB, ModBlocks.DARK_SAND_SET.slab());
        add(ModBlockTags.DECAYS_TO_UNRAVELED_STAIRS, ModBlocks.DARK_SAND_SET.stairs());
        add(ModBlockTags.DECAYS_TO_UNRAVELED_WALL, ModBlocks.DARK_SAND_SET.wall());
        add(ModBlockTags.DECAYS_TO_UNRAVELED_SPIKE,
                Blocks.LIGHTNING_ROD,
                Blocks.LANTERN,
                Blocks.IRON_BARS,
                Blocks.CHAIN,
                Blocks.END_ROD,
                Blocks.POINTED_DRIPSTONE,
                Blocks.TORCH
        ).addOptionalTag(BlockTags.FLOWER_POTS.location()).addOptionalTag(BlockTags.CANDLES.location());
        tag(ModBlockTags.DECAYS_TO_WITHER_ROSE)
                .addOptional(ResourceLocation.parse("minecraft:dandelion"))
                .addOptional(ResourceLocation.parse("minecraft:poppy"))
                .addOptional(ResourceLocation.parse("minecraft:blue_orchid"))
                .addOptional(ResourceLocation.parse("minecraft:allium"))
                .addOptional(ResourceLocation.parse("minecraft:azure_bluet"))
                .addOptional(ResourceLocation.parse("minecraft:red_tulip"))
                .addOptional(ResourceLocation.parse("minecraft:orange_tulip"))
                .addOptional(ResourceLocation.parse("minecraft:white_tulip"))
                .addOptional(ResourceLocation.parse("minecraft:pink_tulip"))
                .addOptional(ResourceLocation.parse("minecraft:oxeye_daisy"))
                .addOptional(ResourceLocation.parse("minecraft:cornflower"))
                .addOptional(ResourceLocation.parse("minecraft:lily_of_the_valley"))
                .addOptional(ResourceLocation.parse("minecraft:torchflower"))
                .addOptional(ResourceLocation.parse("minecraft:sunflower"))
                .addOptional(ResourceLocation.parse("minecraft:lilac"))
                .addOptional(ResourceLocation.parse("minecraft:rose_bush"))
                .addOptional(ResourceLocation.parse("minecraft:peony"));
        add(ModBlockTags.DECAYS_TO_GLASS,
                Blocks.TINTED_GLASS,
                Blocks.REDSTONE_LAMP,
                Blocks.WHITE_STAINED_GLASS,
                Blocks.ORANGE_STAINED_GLASS,
                Blocks.MAGENTA_STAINED_GLASS,
                Blocks.LIGHT_BLUE_STAINED_GLASS,
                Blocks.YELLOW_STAINED_GLASS,
                Blocks.LIME_STAINED_GLASS,
                Blocks.PINK_STAINED_GLASS,
                Blocks.GRAY_STAINED_GLASS,
                Blocks.LIGHT_GRAY_STAINED_GLASS,
                Blocks.CYAN_STAINED_GLASS,
                Blocks.PURPLE_STAINED_GLASS,
                Blocks.BLUE_STAINED_GLASS,
                Blocks.BROWN_STAINED_GLASS,
                Blocks.GREEN_STAINED_GLASS,
                Blocks.RED_STAINED_GLASS,
                Blocks.BLACK_STAINED_GLASS
        );
        add(ModBlockTags.DECAYS_TO_RED_SAND_SLAB, Blocks.RED_SANDSTONE_SLAB);
        add(ModBlockTags.DECAYS_TO_RED_SAND_STAIRS, Blocks.RED_SANDSTONE_STAIRS);
        add(ModBlockTags.DECAYS_TO_RED_SAND_WALL, Blocks.RED_SANDSTONE_WALL);
        add(ModBlockTags.DECAYS_TO_SAND_SLAB, Blocks.SANDSTONE_SLAB);
        add(ModBlockTags.DECAYS_TO_SAND_STAIRS, Blocks.SANDSTONE_STAIRS);
        add(ModBlockTags.DECAYS_TO_SAND_WALL, Blocks.SANDSTONE_WALL);
        add(ModBlockTags.DECAYS_TO_STONE,
                ModBlocks.AMALGAM_BLOCK,
                ModBlocks.AMALGAM_ORE,
                Blocks.BASALT,
                Blocks.BLACKSTONE,
                Blocks.CALCITE,
                ModBlocks.CLOD_ORE,
                Blocks.COBBLESTONE,
                Blocks.DEEPSLATE,
                Blocks.END_STONE,
                Blocks.GLOWSTONE,
                Blocks.NETHERRACK,
                Blocks.OBSIDIAN,
                Blocks.PRISMARINE,
                Blocks.SMOOTH_STONE,
                Blocks.STONE_BRICKS,
                Blocks.TUFF
        );
        add(ModBlockTags.DECAYS_TO_STONE_SLAB,
                Blocks.STONE_BRICK_SLAB,
                Blocks.COBBLESTONE_SLAB,
                Blocks.SMOOTH_STONE_SLAB
        );
        add(ModBlockTags.DECAYS_TO_STONE_STAIRS, Blocks.STONE_BRICK_STAIRS);
        add(ModBlockTags.DECAYS_TO_STONE_WALL, Blocks.STONE_BRICK_WALL);
        add(ModBlockTags.DECAYS_TO_AMALGAM,
                Blocks.CAULDRON,
                Blocks.BELL,
                Blocks.IRON_BLOCK,
                Blocks.COPPER_BLOCK,
                Blocks.CUT_COPPER,
                Blocks.GOLD_BLOCK,
                Blocks.ANCIENT_DEBRIS
        );
        add(ModBlockTags.DECAYS_TO_AMALGAM_ORE,
                Blocks.RAW_COPPER_BLOCK,
                Blocks.COPPER_ORE,
                Blocks.DEEPSLATE_COPPER_ORE,
                Blocks.RAW_IRON_BLOCK,
                Blocks.IRON_ORE,
                Blocks.DEEPSLATE_IRON_ORE,
                Blocks.RAW_GOLD_BLOCK,
                Blocks.GOLD_ORE,
                Blocks.NETHER_GOLD_ORE,
                Blocks.DEEPSLATE_GOLD_ORE
        );
        add(ModBlockTags.DECAYS_TO_CLOD_ORE,
                Blocks.COAL_ORE,
                Blocks.DEEPSLATE_COAL_ORE,
                Blocks.DIAMOND_ORE,
                Blocks.DEEPSLATE_DIAMOND_ORE,
                Blocks.EMERALD_ORE,
                Blocks.DEEPSLATE_EMERALD_ORE,
                Blocks.LAPIS_ORE,
                Blocks.DEEPSLATE_LAPIS_ORE,
                Blocks.NETHER_QUARTZ_ORE
        );
        add(ModBlockTags.DECAYS_TO_CLOD_BLOCK,
                Blocks.AMETHYST_BLOCK,
                Blocks.DIAMOND_BLOCK,
                Blocks.EMERALD_BLOCK,
                Blocks.LAPIS_BLOCK,
                Blocks.QUARTZ_BLOCK
        );
        add(ModBlockTags.DECAYS_TO_COBBLESTONE,
                ModBlocks.CLOD_BLOCK,
                Blocks.GRAVEL,
                Blocks.DIORITE,
                Blocks.DRIPSTONE_BLOCK,
                Blocks.FURNACE
        );
        add(ModBlockTags.DECAYS_TO_COBBLESTONE_SLAB, Blocks.STONECUTTER);
        add(ModBlockTags.DECAYS_TO_FURNACE,
                Blocks.BLAST_FURNACE,
                Blocks.SMOKER,
                Blocks.DROPPER,
                Blocks.OBSERVER,
                Blocks.LODESTONE
        );
        add(ModBlockTags.DECAYS_TO_DEEPSLATE,
                Blocks.COBBLED_DEEPSLATE,
                Blocks.POLISHED_DEEPSLATE
        );
        add(ModBlockTags.DECAYS_TO_DEEPSLATE_SLAB,
                Blocks.COBBLED_DEEPSLATE_SLAB,
                Blocks.POLISHED_DEEPSLATE_SLAB
        );
        add(ModBlockTags.DECAYS_TO_DEEPSLATE_STAIRS,
                Blocks.COBBLED_DEEPSLATE_STAIRS,
                Blocks.POLISHED_DEEPSLATE_STAIRS
        );
        add(ModBlockTags.DECAYS_TO_DEEPSLATE_WALL,
                Blocks.COBBLED_DEEPSLATE_WALL,
                Blocks.POLISHED_DEEPSLATE_WALL
        );
        add(ModBlockTags.DECAYS_TO_ENDSTONE, Blocks.END_STONE_BRICKS);
        add(ModBlockTags.DECAYS_TO_ENDSTONE_SLAB, Blocks.END_STONE_BRICK_SLAB);
        add(ModBlockTags.DECAYS_TO_ENDSTONE_STAIRS, Blocks.END_STONE_BRICK_STAIRS);
        add(ModBlockTags.DECAYS_TO_ENDSTONE_WALL, Blocks.END_STONE_BRICK_WALL);
        add(ModBlockTags.DECAYS_TO_NETHERRACK,
                Blocks.NETHER_BRICKS,
                Blocks.RED_NETHER_BRICKS
        );
        add(ModBlockTags.DECAYS_TO_NETHERRACK_FENCE, Blocks.NETHER_BRICK_FENCE);
        add(ModBlockTags.DECAYS_TO_NETHERRACK_SLAB,
                Blocks.NETHER_BRICK_SLAB,
                Blocks.RED_NETHER_BRICK_SLAB
        );
        add(ModBlockTags.DECAYS_TO_NETHERRACK_STAIRS,
                Blocks.NETHER_BRICK_STAIRS,
                Blocks.RED_NETHER_BRICK_STAIRS
        );
        add(ModBlockTags.DECAYS_TO_NETHERRACK_WALL,
                Blocks.NETHER_BRICK_WALL,
                Blocks.RED_NETHER_BRICK_WALL
        );
        add(ModBlockTags.DECAYS_TO_PRISMARINE,
                Blocks.DARK_PRISMARINE,
                Blocks.SEA_LANTERN
        );
        add(ModBlockTags.DECAYS_TO_PRISMARINE_SLAB,
                Blocks.PRISMARINE_BRICK_SLAB,
                Blocks.DARK_PRISMARINE_SLAB
        );
        add(ModBlockTags.DECAYS_TO_PRISMARINE_STAIRS,
                Blocks.PRISMARINE_BRICK_STAIRS,
                Blocks.DARK_PRISMARINE_STAIRS
        );
        add(ModBlockTags.DECAYS_TO_OBSIDIAN,
                Blocks.CRYING_OBSIDIAN,
                Blocks.MAGMA_BLOCK,
                Blocks.ENCHANTING_TABLE,
                Blocks.ENDER_CHEST
        );
        add(ModBlockTags.DECAYS_TO_STONE_BRICKS,
                Blocks.CHISELED_STONE_BRICKS,
                Blocks.CRACKED_STONE_BRICKS,
                Blocks.MOSSY_STONE_BRICKS
        );
        add(ModBlockTags.DECAYS_TO_STONE_BRICK_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB);
        add(ModBlockTags.DECAYS_TO_STONE_BRICK_STAIRS, Blocks.MOSSY_STONE_BRICK_STAIRS);
        add(ModBlockTags.DECAYS_TO_STONE_BRICK_WALL, Blocks.MOSSY_STONE_BRICK_WALL);
        add(ModBlockTags.DECAYS_TO_BLACKSTONE, Blocks.POLISHED_BLACKSTONE);
        add(ModBlockTags.DECAYS_TO_BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB);
        add(ModBlockTags.DECAYS_TO_BLACKSTONE_STAIRS, Blocks.POLISHED_BLACKSTONE_STAIRS);
        add(ModBlockTags.DECAYS_TO_BLACKSTONE_WALL, Blocks.POLISHED_BLACKSTONE_WALL);
        add(ModBlockTags.DECAYS_TO_DIORITE,
                Blocks.ANDESITE,
                Blocks.GRANITE
        );
        add(ModBlockTags.DECAYS_TO_BASALT,
                Blocks.POLISHED_BASALT,
                Blocks.SMOOTH_BASALT
        );

        add(ModBlockTags.DECAYS_TO_CHEST,
                Blocks.TRAPPED_CHEST,
                Blocks.JUKEBOX,
                Blocks.HOPPER
        ).addOptionalTag(BlockTags.SHULKER_BOXES.location());
        addOptional(ModBlockTags.DECAYS_TO_DRIFTWOOD_LEAVES,
                "oak_leaves",
                "spruce_leaves",
                "birch_leaves",
                "jungle_leaves",
                "acacia_leaves",
                "dark_oak_leaves",
                "mangrove_leaves",
                "cherry_leaves",
                "azalea_leaves",
                "flowering_azalea_leaves"
        );
        addOptional(ModBlockTags.DECAYS_TO_DRIFTWOOD_SAPLING,
                "oak_sapling",
                "spruce_sapling",
                "birch_sapling",
                "jungle_sapling",
                "acacia_sapling",
                "dark_oak_sapling",
                "mangrove_propagule",
                "cherry_sapling",
                "azalea",
                "flowering_azalea"
        );
        addOptional(ModBlockTags.DECAYS_TO_DRIFTWOOD_TRAPDOOR,
                "oak_trapdoor",
                "spruce_trapdoor",
                "birch_trapdoor",
                "jungle_trapdoor",
                "acacia_trapdoor",
                "dark_oak_trapdoor",
                "mangrove_trapdoor",
                "cherry_trapdoor",
                "bamboo_trapdoor",
                "crimson_trapdoor",
                "warped_trapdoor"
        );
        add(ModBlockTags.DECAYS_TO_MOSS_CARPET,
                Blocks.GLOW_LICHEN,
                Blocks.VINE,
                Blocks.CAVE_VINES,
                Blocks.CAVE_VINES_PLANT,
                ModBlocks.DRIFTWOOD_TRAPDOOR
        ).addOptionalTag(BlockTags.WOOL_CARPETS.location());
        add(ModBlockTags.DECAYS_TO_RAIL,
                Blocks.ACTIVATOR_RAIL,
                Blocks.DETECTOR_RAIL,
                Blocks.POWERED_RAIL
        );
        add(ModBlockTags.DECAYS_TO_TO_GLASS_PANE,
                Blocks.TINTED_GLASS,
                Blocks.WHITE_STAINED_GLASS_PANE,
                Blocks.ORANGE_STAINED_GLASS_PANE,
                Blocks.MAGENTA_STAINED_GLASS_PANE,
                Blocks.LIGHT_BLUE_STAINED_GLASS_PANE,
                Blocks.YELLOW_STAINED_GLASS_PANE,
                Blocks.LIME_STAINED_GLASS_PANE,
                Blocks.PINK_STAINED_GLASS_PANE,
                Blocks.GRAY_STAINED_GLASS_PANE,
                Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
                Blocks.CYAN_STAINED_GLASS_PANE,
                Blocks.PURPLE_STAINED_GLASS_PANE,
                Blocks.BLUE_STAINED_GLASS_PANE,
                Blocks.BROWN_STAINED_GLASS_PANE,
                Blocks.GREEN_STAINED_GLASS_PANE,
                Blocks.RED_STAINED_GLASS_PANE,
                Blocks.BLACK_STAINED_GLASS_PANE
        );

        add(ModBlockTags.DECAYS_TO_DIRT,
                Blocks.GRASS_BLOCK,
                Blocks.ROOTED_DIRT,
                Blocks.MYCELIUM,
                Blocks.PODZOL
        );
        addOptional(ModBlockTags.DECAYS_TO_DIRT,
                "short_grass"
        );

        add(ModBlockTags.DECAYS_TO_NETHERWART_BLOCK,
                Blocks.BROWN_MUSHROOM_BLOCK,
                Blocks.RED_MUSHROOM_BLOCK,
                Blocks.MUSHROOM_STEM,
                Blocks.SHROOMLIGHT,
                Blocks.WARPED_WART_BLOCK
        );

        add(ModBlockTags.DECAYS_TO_SKELETON_SKULL,
                Blocks.WITHER_SKELETON_SKULL,
                Blocks.PLAYER_HEAD,
                Blocks.ZOMBIE_HEAD,
                Blocks.CREEPER_HEAD,
                Blocks.DRAGON_HEAD,
                Blocks.PIGLIN_HEAD
        );

        add(ModBlockTags.DECAYS_TO_SKELETON_WALL_SKULL,
                Blocks.WITHER_SKELETON_WALL_SKULL,
                Blocks.PLAYER_WALL_HEAD,
                Blocks.ZOMBIE_WALL_HEAD,
                Blocks.CREEPER_WALL_HEAD,
                Blocks.DRAGON_WALL_HEAD,
                Blocks.PIGLIN_WALL_HEAD
        );

        add(ModBlockTags.DECAYS_TO_MUD_FENCE,
                ModBlocks.DRIFTWOOD_FENCE,
                Blocks.BAMBOO,
                Blocks.SCAFFOLDING,
                Blocks.CHORUS_PLANT,
                Blocks.CHORUS_FLOWER
        );
        add(ModBlockTags.DECAYS_TO_MUD_GATE, ModBlocks.DRIFTWOOD_GATE);
        add(ModBlockTags.DECAYS_TO_MUD_BUTTON, ModBlocks.DRIFTWOOD_BUTTON);
        add(ModBlockTags.DECAYS_TO_MUD_SLAB,
                ModBlocks.DRIFTWOOD_SLAB,
                Blocks.MUD_BRICK_SLAB,
                Blocks.PURPUR_SLAB,
                Blocks.DAYLIGHT_DETECTOR
        );
        add(ModBlockTags.DECAYS_TO_MUD_STAIRS,
                ModBlocks.DRIFTWOOD_STAIRS,
                Blocks.MUD_BRICK_STAIRS,
                Blocks.PURPUR_STAIRS
        );
        add(ModBlockTags.DECAYS_TO_MUD_WALL, Blocks.MUD_BRICK_WALL);

        addOptional(ModBlockTags.DECAYS_TO_DRIFTWOOD_LOG,
                "oak_log",
                "spruce_log",
                "birch_log",
                "jungle_log",
                "acacia_log",
                "dark_oak_log",
                "mangrove_log",
                "cherry_log",
                "crimson_stem",
                "warped_stem"
        );

        addOptional(ModBlockTags.DECAYS_TO_DRIFTWOOD_PLANK,
                "oak_planks",
                "spruce_planks",
                "birch_planks",
                "jungle_planks",
                "acacia_planks",
                "dark_oak_planks",
                "mangrove_planks",
                "cherry_planks",
                "bamboo_planks",
                "crimson_planks",
                "warped_planks"
        );
        add(ModBlockTags.DECAYS_TO_DRIFTWOOD_PLANK,
                Blocks.CRAFTING_TABLE,
                Blocks.CARTOGRAPHY_TABLE,
                Blocks.FLETCHING_TABLE,
                Blocks.SMITHING_TABLE,
                Blocks.LOOM,
                Blocks.CAMPFIRE,
                Blocks.SOUL_CAMPFIRE
        );

        addOptional(ModBlockTags.DECAYS_TO_DRIFTWOOD_FENCE,
                "oak_fence",
                "spruce_fence",
                "birch_fence",
                "jungle_fence",
                "acacia_fence",
                "dark_oak_fence",
                "mangrove_fence",
                "cherry_fence",
                "bamboo_fence",
                "crimson_fence",
                "warped_fence"
        );

        addOptional(ModBlockTags.DECAYS_TO_DRIFTWOOD_GATE,
                "oak_fence_gate",
                "spruce_fence_gate",
                "birch_fence_gate",
                "jungle_fence_gate",
                "acacia_fence_gate",
                "dark_oak_fence_gate",
                "mangrove_fence_gate",
                "cherry_fence_gate",
                "bamboo_fence_gate",
                "crimson_fence_gate",
                "warped_fence_gate"
        );

        addOptional(ModBlockTags.DECAYS_TO_DRIFTWOOD_BUTTON,
                "oak_button",
                "spruce_button",
                "birch_button",
                "jungle_button",
                "acacia_button",
                "dark_oak_button",
                "mangrove_button",
                "cherry_button",
                "bamboo_button",
                "crimson_button",
                "warped_button"
        );

        addOptional(ModBlockTags.DECAYS_TO_DRIFTWOOD_SLAB,
                "oak_slab",
                "spruce_slab",
                "birch_slab",
                "jungle_slab",
                "acacia_slab",
                "dark_oak_slab",
                "mangrove_slab",
                "cherry_slab",
                "bamboo_slab",
                "crimson_slab",
                "warped_slab"
        );

        addOptional(ModBlockTags.DECAYS_TO_DRIFTWOOD_STAIRS,
                "oak_stairs",
                "spruce_stairs",
                "birch_stairs",
                "jungle_stairs",
                "acacia_stairs",
                "dark_oak_stairs",
                "mangrove_stairs",
                "cherry_stairs",
                "bamboo_stairs",
                "crimson_stairs",
                "warped_stairs"
        );

        addOptional(ModBlockTags.DECAYS_TO_DRIFTWOOD_DOOR,
                "oak_door",
                "spruce_door",
                "birch_door",
                "jungle_door",
                "acacia_door",
                "dark_oak_door",
                "mangrove_door",
                "cherry_door",
                "bamboo_door",
                "crimson_door",
                "warped_door"
        );

//
//        add(ModBlockTags.DECAYS_TO_AIR,
//                Blocks.COBWEB,
////                ModBlockTags.MINOR_PLANTS,
//                Blocks.SUGAR_CANE,
//                ModBlocks.DRIFTWOOD_LEAVES,
//                ModBlocks.DRIFTWOOD_SAPLING,
//                Blocks.WITHER_ROSE
//        );
//
//        add(ModBlockTags.DECAYS_TO_GRITTY_STONE,
//                Blocks.INFESTED_STONE,
//                Blocks.INFESTED_COBBLESTONE,
//                Blocks.INFESTED_STONE_BRICKS,
//                Blocks.INFESTED_MOSSY_STONE_BRICKS,
//                Blocks.INFESTED_CRACKED_STONE_BRICKS,
//                Blocks.INFESTED_CHISELED_STONE_BRICKS
//        );

//        add(ModBlockTags.DECAYS_TO_LINT_LAYER,
//                Blocks.MOSS_CARPET,
//                Blocks.RAIL,
//                ModBlocks.DARK_SAND_LAYER,
//                Blocks.REDSTONE_WIRE
//        );

//        add(ModBlockTags.DECAYS_TO_MOSS_CARPET,
//                BlockTags.WOOL_CARPETS,
//                Blocks.GLOW_LICHEN,
//                Blocks.VINE,
//                BlockTags.CAVE_VINES,
//                ModBlocks.DRIFTWOOD_TRAPDOOR);
//
//        add(ModBlockTags.DECAYS_TO_RUST,
//                Blocks.RAIL,
//                Blocks.REDSTONE_BLOCK,
//                Blocks.REDSTONE_LAMP,
//                Blocks.REDSTONE_TORCH,
//                Blocks.REDSTONE_WIRE,
//                Blocks.REDSTONE_WALL_TORCH,
//                Blocks.LIGHTNING_ROD,
//                Blocks.LANTERN,
//                Blocks.IRON_TRAPDOOR,
//                Blocks.IRON_BARS,
//                Blocks.HOPPER,
//                Blocks.CHAIN,
//                BlockTags.CAULDRONS,
//                Blocks.BELL
//        );
//
//        add(ModBlockTags.DECAYS_TO_RAIL,
//                Blocks.ACTIVATOR_RAIL,
//                Blocks.DETECTOR_RAIL,
//                Blocks.POWERED_RAIL);
//
//        add(ModBlockTags.DECAYS_TO_SOLID_STATIC,
//                Blocks.BEDROCK,
//                Blocks.END_PORTAL_FRAME,
//                Blocks.COMMAND_BLOCK,
//                Blocks.CHAIN_COMMAND_BLOCK,
//                Blocks.REPEATING_COMMAND_BLOCK
//        );
//        add(ModBlockTags.DECAYS_TO_UNRAVELED_FENCE,
//                ModBlocks.CLAY_FENCE.get(),
//                ModBlocks.DARK_SAND_FENCE.get()
//        );
//
//        add(ModBlockTags.DECAYS_TO_UNRAVELED_GATE,
//                ModBlocks.CLAY_GATE
//        );
//
//
//        add(ModBlockTags.DECAYS_TO_UNRAVELED_BUTTON,
//                ModBlocks.CLAY_BUTTON.get(),
//                ModBlocks.DARK_SAND_BUTTON.get()
//        );
//
//                add(ModBlockTags.DECAYS_TO_UNRAVELED_SLAB,
//                ModBlocks.CLAY_SLAB.get(),
//                ModBlocks.DARK_SAND_SLAB.get()
//        );
//        add(ModBlockTags.DECAYS_TO_UNRAVELED_STAIRS,
//                ModBlocks.CLAY_STAIRS.get(),
//                ModBlocks.DARK_SAND_STAIRS.get()
//        );
//        add(ModBlockTags.DECAYS_TO_TO_GLASS_PANE,
//                Blocks.GRAY_STAINED_GLASS_PANE,
//                Blocks.BLACK_STAINED_GLASS_PANE,
//                Blocks.ORANGE_STAINED_GLASS_PANE,
//                Blocks.BLUE_STAINED_GLASS_PANE,
//                Blocks.BROWN_STAINED_GLASS_PANE,
//                Blocks.CYAN_STAINED_GLASS_PANE,
//                Blocks.GREEN_STAINED_GLASS_PANE,
//                Blocks.LIGHT_BLUE_STAINED_GLASS_PANE,
//                Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
//                Blocks.LIME_STAINED_GLASS_PANE,
//                Blocks.MAGENTA_STAINED_GLASS_PANE,
//                Blocks.PINK_STAINED_GLASS_PANE,
//                Blocks.PURPLE_STAINED_GLASS_PANE,
//                Blocks.RED_STAINED_GLASS_PANE,
//                Blocks.WHITE_STAINED_GLASS_PANE,
//                Blocks.YELLOW_STAINED_GLASS_PANE
//        );
//        add(ModBlockTags.DECAYS_TO_RUST,
//                //REDSTONE VARIANTS
//                Blocks.LIGHTNING_ROD,
//                Blocks.LANTERN,
//                Blocks.IRON_BARS,
//                Blocks.HOPPER,
//                Blocks.CHAIN,
//                Blocks.CAULDRON,
//                Blocks.BELL
//        );
//        add(ModBlockTags.DECAYS_TO_UNRAVELED_SPIKE,
//                Blocks.END_ROD,
//                Blocks.POINTED_DRIPSTONE
//        ).addOptionalTag(BlockTags.FLOWER_POTS.location()).addOptionalTag(BlockTags.CANDLES.location());
//        tag(ModBlockTags.DECAYS_TO_WITHER_ROSE).addOptionalTag(BlockTags.SMALL_FLOWERS.location()).addOptionalTag(BlockTags.TALL_FLOWERS.location());
//        add(ModBlockTags.DECAYS_TO_CLAY,
//                ModBlocks.AMALGAM_BLOCK.get(),
//                Blocks.MUD,
//                Blocks.TERRACOTTA,
//                Blocks.BRICKS
//        );
//        add(ModBlockTags.DECAYS_TO_CLAY_FENCE,
//                ModBlocks.CLAY_FENCE.get(),
//                ModBlocks.MUD_FENCE.get()
//        );
//        add(ModBlockTags.DECAYS_TO_CLAY_GATE,
//                ModBlocks.CLAY_GATE.get(),
//                ModBlocks.MUD_GATE.get()
//        );
//
//        add(ModBlockTags.DECAYS_TO_CLAY_WALL,
//                Blocks.BRICK_WALL
//        );
//
//        add(ModBlockTags.DECAYS_TO_CLAY_BUTTON,
//                ModBlocks.CLAY_BUTTON.get(),
//                ModBlocks.MUD_BUTTON.get()
//        );
//        add(ModBlockTags.DECAYS_TO_CLAY_SLAB,
//                Blocks.BRICK_SLAB,
//                ModBlocks.MUD_SLAB.get(),
//                ModBlocks.AMALGAM_SLAB.get()
//        );
//        add(ModBlockTags.DECAYS_TO_CLAY_STAIRS,
//                Blocks.BRICK_STAIRS,
//                ModBlocks.MUD_STAIRS.get(),
//                ModBlocks.AMALGAM_STAIRS.get()
//        );
//
//        add(ModBlockTags.DECAYS_TO_DARK_SAND,
//                Blocks.AMETHYST_BLOCK,
//                Blocks.GLASS,
//                Blocks.GRAVEL,
//                Blocks.RED_SAND,
//                Blocks.SAND,
//                Blocks.SOUL_SAND
//        );
//
//        add(ModBlockTags.DECAYS_TO_DARK_SAND_FENCE,
//                ModBlocks.GRAVEL_FENCE
//        );
//
//
//        add(ModBlockTags.DECAYS_TO_UNRAVELED_FABRIC,
//                ModBlocks.DARK_SAND.get(),
//                Blocks.CLAY);
//
//        add(ModBlockTags.DECAYS_TO_MUD,
//                Blocks.DIRT,
//                Blocks.GRASS_BLOCK,
//                Blocks.PODZOL,
//                Blocks.MYCELIUM,
//                ModBlocks.DRIFTWOOD_PLANKS.get(),
//                Blocks.COAL_BLOCK,
//                Blocks.COMPOSTER,
//                Blocks.CHEST,
//                Blocks.BONE_BLOCK,
//                Blocks.SKELETON_SKULL,
//                Blocks.SKELETON_WALL_SKULL,
//                Blocks.WITHER_SKELETON_SKULL,
//                Blocks.WITHER_SKELETON_WALL_SKULL,
//                Blocks.DRAGON_HEAD,
//                Blocks.DRAGON_WALL_HEAD,
//                Blocks.CACTUS,
//                Blocks.COCOA,
//                Blocks.PUMPKIN,
//                Blocks.MELON,
//                Blocks.HAY_BLOCK,
//                Blocks.MOSS_BLOCK,
//                Blocks.SLIME_BLOCK,
//                Blocks.HONEYCOMB_BLOCK,
//                Blocks.LECTERN,
//                Blocks.PURPUR_BLOCK,
//                Blocks.DRIED_KELP_BLOCK,
//                Blocks.NETHER_WART_BLOCK,
//                Blocks.PACKED_MUD);
//
//        add(ModBlockTags.DECAYS_TO_NETHERWART_BLOCK,
//                Blocks.BROWN_MUSHROOM_BLOCK,
//                Blocks.RED_MUSHROOM_BLOCK);
//
//        add(ModBlockTags.DECAYS_TO_GLASS,
//                Blocks.TINTED_GLASS,
//                Blocks.REDSTONE_BLOCK,
//                Blocks.GRAY_STAINED_GLASS,
//                Blocks.BLACK_STAINED_GLASS,
//                Blocks.ORANGE_STAINED_GLASS,
//                Blocks.BLUE_STAINED_GLASS,
//                Blocks.BROWN_STAINED_GLASS,
//                Blocks.CYAN_STAINED_GLASS,
//                Blocks.GREEN_STAINED_GLASS,
//                Blocks.LIGHT_BLUE_STAINED_GLASS,
//                Blocks.LIGHT_GRAY_STAINED_GLASS,
//                Blocks.LIME_STAINED_GLASS,
//                Blocks.MAGENTA_STAINED_GLASS,
//                Blocks.PINK_STAINED_GLASS,
//                Blocks.PURPLE_STAINED_GLASS,
//                Blocks.RED_STAINED_GLASS,
//                Blocks.WHITE_STAINED_GLASS,
//                Blocks.YELLOW_STAINED_GLASS);
//
//        add(ModBlockTags.DECAYS_TO_GRAVEL,
//                ModBlocks.AMALGAM_BLOCK.get(),
//                ModBlocks.CLOD_ORE.get(),
//                Blocks.COBBLESTONE);
//
//        add(ModBlockTags.DECAYS_TO_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
//
//        add(ModBlockTags.DECAYS_TO_AMALGAM_ORE, Blocks.RAW_COPPER_BLOCK, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.RAW_IRON_BLOCK, Blocks.DEEPSLATE_IRON_ORE, Blocks.IRON_ORE, Blocks.RAW_GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.NETHER_GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE);
//
//        add(ModBlockTags.DECAYS_TO_CLOD_ORE, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE, Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.NETHER_QUARTZ_ORE);
//
//        add(ModBlockTags.DECAYS_TO_COBBLESTONE,
//                Blocks.ANDESITE,
//                Blocks.BASALT,
//                Blocks.BLACKSTONE,
//                Blocks.CALCITE,
//                Blocks.DEEPSLATE,
//                Blocks.DIORITE,
//                Blocks.DRIPSTONE_BLOCK,
//                Blocks.END_STONE,
//                Blocks.FURNACE,
//                Blocks.GRANITE,
//                Blocks.NETHERRACK,
//                Blocks.PRISMARINE,
//                Blocks.STONE,
//                Blocks.TUFF);
//
//        add(ModBlockTags.DECAYS_TO_COBBLESTONE_SLAB, Blocks.STONE_SLAB, Blocks.STONECUTTER);
//
//        add(ModBlockTags.DECAYS_TO_STONE, ModBlocks.CLOD_BLOCK.get(), Blocks.CRACKED_STONE_BRICKS, Blocks.GLOWSTONE, Blocks.OBSIDIAN, Blocks.REDSTONE_BLOCK);
//
//        tag(ModBlockTags.DECAYS_TO_DARK_SAND_SLAB);
//        tag(ModBlockTags.DECAYS_TO_DARK_SAND_STAIRS);
//        tag(ModBlockTags.DECAYS_TO_DARK_SAND_WALL);
//
//        add(ModBlockTags.DECAYS_TO_AMALGAM_DOOR,
//                Blocks.IRON_DOOR,
//                Blocks.COPPER_DOOR,
//                ModBlocks.GOLD_DOOR
//        );
//
//        add(ModBlockTags.DECAYS_TO_AMALGAM,
//                Blocks.IRON_BLOCK,
//                Blocks.COPPER_BLOCK,
//                Blocks.CUT_COPPER,
//                Blocks.GOLD_BLOCK);
//
//
//        add(ModBlockTags.DECAYS_TO_DRIFTWOOD_PLANK).addOptionalTag(BlockTags.PLANKS.location());

        add(ModBlocks.DRIFTWOOD_LOG,
                BlockTags.COMPLETES_FIND_TREE_TUTORIAL,
                BlockTags.SNAPS_GOAT_HORN,
                BlockTags.LOGS_THAT_BURN,
                BlockTags.MINEABLE_WITH_AXE,
                BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE,
                ModBlockTags.DRIFTWOOD_LOGS,
                BlockTags.LOGS,
                BlockTags.PARROTS_SPAWNABLE_ON
        );

        add(ModBlocks.DRIFTWOOD_PLANKS,
                BlockTags.PLANKS,
                BlockTags.MINEABLE_WITH_AXE
        );

        add(ModBlocks.DRIFTWOOD_LEAVES,
                BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE,
                BlockTags.COMPLETES_FIND_TREE_TUTORIAL,
                BlockTags.LEAVES,
                BlockTags.MINEABLE_WITH_HOE,
                BlockTags.PARROTS_SPAWNABLE_ON,
                BlockTags.REPLACEABLE_BY_TREES,
                BlockTags.SWORD_EFFICIENT
        );

        add(ModBlocks.DRIFTWOOD_SAPLING,
                BlockTags.SAPLINGS,
                BlockTags.MINEABLE_WITH_AXE,
                BlockTags.SWORD_EFFICIENT
        );

        add(ModBlocks.DRIFTWOOD_FENCE,
                ConventionalBlockTags.WOODEN_FENCES,
                BlockTags.WOODEN_FENCES,
                ConventionalBlockTags.FENCES,
                BlockTags.WOODEN_FENCES,
                BlockTags.MINEABLE_WITH_AXE
        );

        add(ModBlocks.DRIFTWOOD_GATE,
                ConventionalBlockTags.FENCE_GATES,
                BlockTags.FENCE_GATES,
                ConventionalBlockTags.WOODEN_FENCE_GATES,
                BlockTags.UNSTABLE_BOTTOM_CENTER,
                BlockTags.MINEABLE_WITH_AXE
        );

        add(ModBlocks.DRIFTWOOD_BUTTON,
                BlockTags.WOODEN_FENCES,
                BlockTags.MINEABLE_WITH_AXE,
                BlockTags.BUTTONS
        );

        add(ModBlocks.DRIFTWOOD_SLAB,
                BlockTags.MINEABLE_WITH_AXE,
                BlockTags.SLABS,
                BlockTags.WOODEN_SLABS
        );

        add(ModBlocks.DRIFTWOOD_STAIRS,
                BlockTags.MINEABLE_WITH_AXE,
                BlockTags.WOODEN_STAIRS,
                BlockTags.STAIRS
        );

        add(ModBlocks.DRIFTWOOD_DOOR,
                BlockTags.MINEABLE_WITH_AXE,
                BlockTags.WOODEN_DOORS,
                BlockTags.DOORS,
                BlockTags.MOB_INTERACTABLE_DOORS
        );

        add(ModBlocks.DRIFTWOOD_TRAPDOOR,
                BlockTags.TRAPDOORS,
                BlockTags.WOODEN_TRAPDOORS,
                BlockTags.MINEABLE_WITH_AXE
        );

        add(BlockTags.DOORS, ModBlocks.STONE_DOOR, ModBlocks.QUARTZ_DOOR, ModBlocks.AMALGAM_DOOR);
        add(BlockTags.TRAPDOORS, ModBlocks.AMALGAM_TRAPDOOR);

        add(ModBlocks.DARK_SAND,
                BlockTags.SMELTS_TO_GLASS,
                ConventionalBlockTags.SANDS,
                BlockTags.CAMEL_SAND_STEP_SOUND_BLOCKS,
                BlockTags.ENDERMAN_HOLDABLE,
                ConventionalBlockTags.SANDS,
                BlockTags.BAMBOO_PLANTABLE_ON,
                BlockTags.MINEABLE_WITH_SHOVEL,
                ConventionalBlockTags.BLACK_DYED,
                BlockTags.SAND,
                BlockTags.DEAD_BUSH_MAY_PLACE_ON,
                ConventionalBlockTags.BLACK_DYED
        );
        add(ModBlocks.PALE_SAND,
                BlockTags.SMELTS_TO_GLASS,
                ConventionalBlockTags.SANDS,
                BlockTags.CAMEL_SAND_STEP_SOUND_BLOCKS,
                BlockTags.ENDERMAN_HOLDABLE,
                BlockTags.BAMBOO_PLANTABLE_ON,
                BlockTags.MINEABLE_WITH_SHOVEL,
                BlockTags.SAND,
                BlockTags.DEAD_BUSH_MAY_PLACE_ON
        );
        add(ModBlocks.DARK_SAND_LAYER, BlockTags.MINEABLE_WITH_SHOVEL);
        add(ModBlocks.LINT_LAYER, BlockTags.MINEABLE_WITH_HOE);

        add(ModBlocks.TESSELATING_LOOM, BlockTags.MINEABLE_WITH_AXE);

        setupSet(ModBlocks.UNRAVELED_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.DEEPSLATE_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.END_STONE_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.NETHERRACK_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.WHITE_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.WHITE_GLAZED_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.ORANGE_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.ORANGE_GLAZED_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.MAGENTA_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.MAGENTA_GLAZED_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.LIGHT_BLUE_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.LIGHT_BLUE_GLAZED_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.YELLOW_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.YELLOW_GLAZED_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.LIME_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.LIME_GLAZED_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.PINK_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.PINK_GLAZED_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.GRAY_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.GRAY_GLAZED_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.LIGHT_GRAY_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.LIGHT_GRAY_GLAZED_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.CYAN_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.CYAN_GLAZED_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.PURPLE_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.PURPLE_GLAZED_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.BLUE_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.BLUE_GLAZED_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.BROWN_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.BROWN_GLAZED_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.GREEN_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.GREEN_GLAZED_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.RED_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.RED_GLAZED_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.BLACK_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);
        setupSet(ModBlocks.BLACK_GLAZED_TERRACOTTA_SET, BlockTags.MINEABLE_WITH_PICKAXE);

        add(BlockTags.MINEABLE_WITH_PICKAXE,
                ModBlocks.REALITY_SPONGE,
                ModBlocks.UNRAVELLED_FABRIC,
                ModBlocks.SOLID_STATIC,
                ModBlocks.RUST,
                ModBlocks.UNRAVELED_SPIKE,
                ModBlocks.GRITTY_STONE
        );

        add(ModBlocks.CLOD_ORE, ConventionalBlockTags.ORES, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.CLOD_BLOCK, BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE);

        add(ModBlocks.WHITE_FABRIC, ConventionalBlockTags.WHITE_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.FABRIC);
        add(ModBlocks.ORANGE_FABRIC, ConventionalBlockTags.ORANGE_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.FABRIC);
        add(ModBlocks.MAGENTA_FABRIC, ConventionalBlockTags.MAGENTA_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.FABRIC);
        add(ModBlocks.LIGHT_BLUE_FABRIC, ConventionalBlockTags.LIGHT_BLUE_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.FABRIC);
        add(ModBlocks.YELLOW_FABRIC, ConventionalBlockTags.YELLOW_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.FABRIC);
        add(ModBlocks.LIME_FABRIC, ConventionalBlockTags.LIME_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.FABRIC);
        add(ModBlocks.PINK_FABRIC, ConventionalBlockTags.PINK_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.FABRIC);
        add(ModBlocks.GRAY_FABRIC, ConventionalBlockTags.GRAY_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.FABRIC);
        add(ModBlocks.LIGHT_GRAY_FABRIC, ConventionalBlockTags.LIGHT_GRAY_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.FABRIC);
        add(ModBlocks.CYAN_FABRIC, ConventionalBlockTags.CYAN_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.FABRIC);
        add(ModBlocks.PURPLE_FABRIC, ConventionalBlockTags.PURPLE_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.FABRIC);
        add(ModBlocks.BLUE_FABRIC, ConventionalBlockTags.BLUE_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.FABRIC);
        add(ModBlocks.BROWN_FABRIC, ConventionalBlockTags.BROWN_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.FABRIC);
        add(ModBlocks.GREEN_FABRIC, ConventionalBlockTags.GREEN_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.FABRIC);
        add(ModBlocks.RED_FABRIC, ConventionalBlockTags.RED_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.FABRIC);
        add(ModBlocks.BLACK_FABRIC, ConventionalBlockTags.BLACK_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.FABRIC);

        add(ModBlocks.WHITE_ANCIENT_FABRIC, ConventionalBlockTags.WHITE_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.ANCIENT_FABRIC);
        add(ModBlocks.ORANGE_ANCIENT_FABRIC, ConventionalBlockTags.ORANGE_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.ANCIENT_FABRIC);
        add(ModBlocks.MAGENTA_ANCIENT_FABRIC, ConventionalBlockTags.MAGENTA_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.ANCIENT_FABRIC);
        add(ModBlocks.LIGHT_BLUE_ANCIENT_FABRIC, ConventionalBlockTags.LIGHT_BLUE_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.ANCIENT_FABRIC);
        add(ModBlocks.YELLOW_ANCIENT_FABRIC, ConventionalBlockTags.YELLOW_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.ANCIENT_FABRIC);
        add(ModBlocks.LIME_ANCIENT_FABRIC, ConventionalBlockTags.LIME_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.ANCIENT_FABRIC);
        add(ModBlocks.PINK_ANCIENT_FABRIC, ConventionalBlockTags.PINK_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.ANCIENT_FABRIC);
        add(ModBlocks.GRAY_ANCIENT_FABRIC, ConventionalBlockTags.GRAY_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.ANCIENT_FABRIC);
        add(ModBlocks.LIGHT_GRAY_ANCIENT_FABRIC, ConventionalBlockTags.LIGHT_GRAY_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.ANCIENT_FABRIC);
        add(ModBlocks.CYAN_ANCIENT_FABRIC, ConventionalBlockTags.CYAN_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.ANCIENT_FABRIC);
        add(ModBlocks.PURPLE_ANCIENT_FABRIC, ConventionalBlockTags.PURPLE_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.ANCIENT_FABRIC);
        add(ModBlocks.BLUE_ANCIENT_FABRIC, ConventionalBlockTags.BLUE_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.ANCIENT_FABRIC);
        add(ModBlocks.BROWN_ANCIENT_FABRIC, ConventionalBlockTags.BROWN_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.ANCIENT_FABRIC);
        add(ModBlocks.GREEN_ANCIENT_FABRIC, ConventionalBlockTags.GREEN_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.ANCIENT_FABRIC);
        add(ModBlocks.RED_ANCIENT_FABRIC, ConventionalBlockTags.RED_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.ANCIENT_FABRIC);
        add(ModBlocks.BLACK_ANCIENT_FABRIC, ConventionalBlockTags.BLACK_DYED, BlockTags.MINEABLE_WITH_PICKAXE, ModBlockTags.ANCIENT_FABRIC);

        add(ModBlocks.AMALGAM_SLAB, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.SLABS);
        add(ModBlocks.AMALGAM_STAIRS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.STAIRS);
        add(ModBlocks.AMALGAM_ORE, ConventionalBlockTags.ORES, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.AMALGAM_BLOCK, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.BEACON_BASE_BLOCKS);
        add(ModBlocks.AMALGAM_TRAPDOOR, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.TRAPDOORS);
        add(ModBlocks.STONE_SLAB, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.SLABS);
        add(ModBlocks.STONE_STAIRS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.STAIRS);
        add(ModBlocks.STONE_WALL, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.WALLS);

        add(ModBlocks.GOLD_DOOR, BlockTags.DOORS, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.STONE_DOOR, BlockTags.DOORS, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.QUARTZ_DOOR, BlockTags.DOORS, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.AMALGAM_DOOR, BlockTags.DOORS, BlockTags.MINEABLE_WITH_PICKAXE);


        setupSet(ModBlocks.GRAVEL_SET, BlockTags.MINEABLE_WITH_SHOVEL);
        setupSet(ModBlocks.GRAVEL_SET, BlockTags.MINEABLE_WITH_SHOVEL);
        setupSet(ModBlocks.DARK_SAND_SET, BlockTags.MINEABLE_WITH_SHOVEL);
        setupSet(ModBlocks.CLAY_SET, BlockTags.MINEABLE_WITH_SHOVEL);
        setupSet(ModBlocks.MUD_SET, BlockTags.MINEABLE_WITH_SHOVEL);
        setupSet(ModBlocks.RED_SAND_SET, BlockTags.MINEABLE_WITH_SHOVEL);
        setupSet(ModBlocks.SAND_SET, BlockTags.MINEABLE_WITH_SHOVEL);
    }

    private void setupSet(ModBlocks.DecayGroupSet set, TagKey<Block> tag) {
        add(set.button(), BlockTags.BUTTONS, tag);
        add(set.wall(), BlockTags.WALLS, tag);
        add(set.fence(), BlockTags.FENCES, tag);
        add(set.gate(), BlockTags.FENCE_GATES, tag);
        add(set.slab(), BlockTags.SLABS, tag);
        add(set.stairs(), BlockTags.STAIRS, tag);
    }

    @SafeVarargs
    private void add(Block block, TagKey<Block>... objects) {
        var key = block.builtInRegistryHolder().key();
        for (TagKey<Block> object : objects)
            tag(object).add(key);
    }

    private TagAppender<Block> add(TagKey<Block> tag, Object... objects) {
        var appender = tag(tag);

        for(var object : objects) {
            if (object instanceof Block block) {
                appender.add(block.builtInRegistryHolder().key());
            } else if (object instanceof TagKey<?> key && key.isFor(Registries.BLOCK)) {
                appender.addTag((TagKey<Block>) key);
            } else if (object instanceof Collection<?> list) {
                for(var element : list) {
                    if (element instanceof Block block) {
                        appender.add(block.builtInRegistryHolder().key());
                    }
                }
            } else if (object instanceof ModBlocks.DecayGroupSet set) {
                appender.add(set.fence().builtInRegistryHolder().key());
                appender.add(set.gate().builtInRegistryHolder().key());
                appender.add(set.button().builtInRegistryHolder().key());
                appender.add(set.slab().builtInRegistryHolder().key());
                appender.add(set.stairs().builtInRegistryHolder().key());
                appender.add(set.wall().builtInRegistryHolder().key());
            }
        }


        return appender;
    }

    private void addOptional(TagKey<Block> tag, String... ids) {
        var appender = tag(tag);

        for (String id : ids) {
            appender.addOptional(id.contains(":") ? ResourceLocation.parse(id) : ResourceLocation.parse("minecraft:" + id));
        }
    }
}
