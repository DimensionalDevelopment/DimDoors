package org.dimdev.dimdoors.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.tag.ModBlockTags;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public BlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        configure(arg.asGetterLookup());
    }

    protected void configure(HolderGetter.Provider arg) {
//
//		add(ModBlockTags.DECAYS_TO_AIR,
//				Blocks.COBWEB,
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
//		add(ModBlockTags.DECAYS_TO_RAIL,
//				Blocks.ACTIVATOR_RAIL,
//				Blocks.DETECTOR_RAIL,
//				Blocks.POWERED_RAIL);
//
//		add(ModBlockTags.DECAYS_TO_SOLID_STATIC,
//				Blocks.BEDROCK,
//				Blocks.END_PORTAL_FRAME,
//				Blocks.COMMAND_BLOCK,
//				Blocks.CHAIN_COMMAND_BLOCK,
//				Blocks.REPEATING_COMMAND_BLOCK
//		);
//		add(ModBlockTags.DECAYS_TO_UNRAVELED_FENCE,
//				ModBlocks.CLAY_FENCE.get(),
//				ModBlocks.DARK_SAND_FENCE.get()
//		);
//
//        add(ModBlockTags.DECAYS_TO_UNRAVELED_GATE,
//                ModBlocks.CLAY_GATE
//        );
//
//
//		add(ModBlockTags.DECAYS_TO_UNRAVELED_BUTTON,
//				ModBlocks.CLAY_BUTTON.get(),
//				ModBlocks.DARK_SAND_BUTTON.get()
//		);
//
//        		add(ModBlockTags.DECAYS_TO_UNRAVELED_SLAB,
//				ModBlocks.CLAY_SLAB.get(),
//				ModBlocks.DARK_SAND_SLAB.get()
//		);
//		add(ModBlockTags.DECAYS_TO_UNRAVELED_STAIRS,
//				ModBlocks.CLAY_STAIRS.get(),
//				ModBlocks.DARK_SAND_STAIRS.get()
//		);
//		add(ModBlockTags.DECAYS_TO_TO_GLASS_PANE,
//				Blocks.GRAY_STAINED_GLASS_PANE,
//				Blocks.BLACK_STAINED_GLASS_PANE,
//				Blocks.ORANGE_STAINED_GLASS_PANE,
//				Blocks.BLUE_STAINED_GLASS_PANE,
//				Blocks.BROWN_STAINED_GLASS_PANE,
//				Blocks.CYAN_STAINED_GLASS_PANE,
//				Blocks.GREEN_STAINED_GLASS_PANE,
//				Blocks.LIGHT_BLUE_STAINED_GLASS_PANE,
//				Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
//				Blocks.LIME_STAINED_GLASS_PANE,
//				Blocks.MAGENTA_STAINED_GLASS_PANE,
//				Blocks.PINK_STAINED_GLASS_PANE,
//				Blocks.PURPLE_STAINED_GLASS_PANE,
//				Blocks.RED_STAINED_GLASS_PANE,
//				Blocks.WHITE_STAINED_GLASS_PANE,
//				Blocks.YELLOW_STAINED_GLASS_PANE
//		);
//		add(ModBlockTags.DECAYS_TO_RUST,
//				//REDSTONE VARIANTS
//				Blocks.LIGHTNING_ROD,
//				Blocks.LANTERN,
//				Blocks.IRON_BARS,
//				Blocks.HOPPER,
//				Blocks.CHAIN,
//				Blocks.CAULDRON,
//				Blocks.BELL
//		);
//		add(ModBlockTags.DECAYS_TO_UNRAVELED_SPIKE,
//				Blocks.END_ROD,
//				Blocks.POINTED_DRIPSTONE
//		).addOptionalTag(BlockTags.FLOWER_POTS.location()).addOptionalTag(BlockTags.CANDLES.location());
//		tag(ModBlockTags.DECAYS_TO_WITHER_ROSE).addOptionalTag(BlockTags.SMALL_FLOWERS.location()).addOptionalTag(BlockTags.TALL_FLOWERS.location());
//		add(ModBlockTags.DECAYS_TO_CLAY,
//				ModBlocks.AMALGAM_BLOCK.get(),
//				Blocks.MUD,
//				Blocks.TERRACOTTA,
//				Blocks.BRICKS
//		);
//		add(ModBlockTags.DECAYS_TO_CLAY_FENCE,
//				ModBlocks.CLAY_FENCE.get(),
//				ModBlocks.MUD_FENCE.get()
//		);
//		add(ModBlockTags.DECAYS_TO_CLAY_GATE,
//				ModBlocks.CLAY_GATE.get(),
//				ModBlocks.MUD_GATE.get()
//		);
//
//        add(ModBlockTags.DECAYS_TO_CLAY_WALL,
//                Blocks.BRICK_WALL
//        );
//
//		add(ModBlockTags.DECAYS_TO_CLAY_BUTTON,
//				ModBlocks.CLAY_BUTTON.get(),
//				ModBlocks.MUD_BUTTON.get()
//		);
//		add(ModBlockTags.DECAYS_TO_CLAY_SLAB,
//				Blocks.BRICK_SLAB,
//				ModBlocks.MUD_SLAB.get(),
//				ModBlocks.AMALGAM_SLAB.get()
//		);
//		add(ModBlockTags.DECAYS_TO_CLAY_STAIRS,
//				Blocks.BRICK_STAIRS,
//				ModBlocks.MUD_STAIRS.get(),
//				ModBlocks.AMALGAM_STAIRS.get()
//		);
//
//        add(ModBlockTags.DECAYS_TO_DARK_SAND,
//				Blocks.AMETHYST_BLOCK,
//				Blocks.GLASS,
//				Blocks.GRAVEL,
//				Blocks.RED_SAND,
//				Blocks.SAND,
//				Blocks.SOUL_SAND
//		);
//
//        add(ModBlockTags.DECAYS_TO_DARK_SAND_FENCE,
//                ModBlocks.GRAVEL_FENCE
//        );
//
//
//		add(ModBlockTags.DECAYS_TO_UNRAVELED_FABRIC,
//				ModBlocks.DARK_SAND.get(),
//				Blocks.CLAY);
//
//		add(ModBlockTags.DECAYS_TO_MUD,
//				Blocks.DIRT,
//				Blocks.GRASS_BLOCK,
//				Blocks.PODZOL,
//				Blocks.MYCELIUM,
//				ModBlocks.DRIFTWOOD_PLANKS.get(),
//				Blocks.COAL_BLOCK,
//				Blocks.COMPOSTER,
//				Blocks.CHEST,
//				Blocks.BONE_BLOCK,
//				Blocks.SKELETON_SKULL,
//				Blocks.SKELETON_WALL_SKULL,
//				Blocks.WITHER_SKELETON_SKULL,
//				Blocks.WITHER_SKELETON_WALL_SKULL,
//				Blocks.DRAGON_HEAD,
//				Blocks.DRAGON_WALL_HEAD,
//				Blocks.CACTUS,
//				Blocks.COCOA,
//				Blocks.PUMPKIN,
//				Blocks.MELON,
//				Blocks.HAY_BLOCK,
//				Blocks.MOSS_BLOCK,
//				Blocks.SLIME_BLOCK,
//				Blocks.HONEYCOMB_BLOCK,
//				Blocks.LECTERN,
//				Blocks.PURPUR_BLOCK,
//				Blocks.DRIED_KELP_BLOCK,
//				Blocks.NETHER_WART_BLOCK,
//				Blocks.PACKED_MUD);
//
//		add(ModBlockTags.DECAYS_TO_NETHERWART_BLOCK,
//				Blocks.BROWN_MUSHROOM_BLOCK,
//				Blocks.RED_MUSHROOM_BLOCK);
//
//		add(ModBlockTags.DECAYS_TO_GLASS,
//				Blocks.TINTED_GLASS,
//				Blocks.REDSTONE_BLOCK,
//				Blocks.GRAY_STAINED_GLASS,
//				Blocks.BLACK_STAINED_GLASS,
//				Blocks.ORANGE_STAINED_GLASS,
//				Blocks.BLUE_STAINED_GLASS,
//				Blocks.BROWN_STAINED_GLASS,
//				Blocks.CYAN_STAINED_GLASS,
//				Blocks.GREEN_STAINED_GLASS,
//				Blocks.LIGHT_BLUE_STAINED_GLASS,
//				Blocks.LIGHT_GRAY_STAINED_GLASS,
//				Blocks.LIME_STAINED_GLASS,
//				Blocks.MAGENTA_STAINED_GLASS,
//				Blocks.PINK_STAINED_GLASS,
//				Blocks.PURPLE_STAINED_GLASS,
//				Blocks.RED_STAINED_GLASS,
//				Blocks.WHITE_STAINED_GLASS,
//				Blocks.YELLOW_STAINED_GLASS);
//
//		add(ModBlockTags.DECAYS_TO_GRAVEL,
//				ModBlocks.AMALGAM_BLOCK.get(),
//				ModBlocks.CLOD_ORE.get(),
//				Blocks.COBBLESTONE);
//
//        add(ModBlockTags.DECAYS_TO_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
//
//		add(ModBlockTags.DECAYS_TO_AMALGAM_ORE, Blocks.RAW_COPPER_BLOCK, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.RAW_IRON_BLOCK, Blocks.DEEPSLATE_IRON_ORE, Blocks.IRON_ORE, Blocks.RAW_GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.NETHER_GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE);
//
//		add(ModBlockTags.DECAYS_TO_CLOD_ORE, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE, Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.NETHER_QUARTZ_ORE);
//
//		add(ModBlockTags.DECAYS_TO_COBBLESTONE,
//				Blocks.ANDESITE,
//				Blocks.BASALT,
//				Blocks.BLACKSTONE,
//				Blocks.CALCITE,
//				Blocks.DEEPSLATE,
//				Blocks.DIORITE,
//				Blocks.DRIPSTONE_BLOCK,
//				Blocks.END_STONE,
//				Blocks.FURNACE,
//				Blocks.GRANITE,
//				Blocks.NETHERRACK,
//				Blocks.PRISMARINE,
//				Blocks.STONE,
//				Blocks.TUFF);
//
//		add(ModBlockTags.DECAYS_TO_COBBLESTONE_SLAB, Blocks.STONE_SLAB, Blocks.STONECUTTER);
//
//		add(ModBlockTags.DECAYS_TO_STONE, ModBlocks.CLOD_BLOCK.get(), Blocks.CRACKED_STONE_BRICKS, Blocks.GLOWSTONE, Blocks.OBSIDIAN, Blocks.REDSTONE_BLOCK);
//
//		tag(ModBlockTags.DECAYS_TO_DARK_SAND_SLAB);
//		tag(ModBlockTags.DECAYS_TO_DARK_SAND_STAIRS);
//		tag(ModBlockTags.DECAYS_TO_DARK_SAND_WALL);
//
//        add(ModBlockTags.DECAYS_TO_AMALGAM_DOOR,
//                Blocks.IRON_DOOR,
//                Blocks.COPPER_DOOR,
//                ModBlocks.GOLD_DOOR
//        );
//
//        add(ModBlockTags.DECAYS_TO_AMALGAM,
//                Blocks.IRON_BLOCK,
//				Blocks.COPPER_BLOCK,
//				Blocks.CUT_COPPER,
//				Blocks.GOLD_BLOCK);
//
//
//		add(ModBlockTags.DECAYS_TO_DRIFTWOOD_PLANK).addOptionalTag(BlockTags.PLANKS.location());

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
        setupSet(ModBlocks.GRAY_GLAZED_TERRACOTTASET, BlockTags.MINEABLE_WITH_PICKAXE);
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
                ModBlocks.RUST,
                ModBlocks.UNRAVELED_SPIKE,
                ModBlocks.GRITTY_STONE
        );

        add(ModBlocks.CLOD_ORE, ConventionalBlockTags.ORES, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.CLOD_BLOCK, BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE);

        add(ModBlocks.WHITE_FABRIC, ConventionalBlockTags.WHITE_DYED, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.ORANGE_FABRIC, ConventionalBlockTags.ORANGE_DYED, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.MAGENTA_FABRIC, ConventionalBlockTags.MAGENTA_DYED, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.LIGHT_BLUE_FABRIC, ConventionalBlockTags.LIGHT_BLUE_DYED, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.YELLOW_FABRIC, ConventionalBlockTags.YELLOW_DYED, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.LIME_FABRIC, ConventionalBlockTags.LIME_DYED, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.PINK_FABRIC, ConventionalBlockTags.PINK_DYED, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.GRAY_FABRIC, ConventionalBlockTags.GRAY_DYED, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.LIGHT_GRAY_FABRIC, ConventionalBlockTags.LIGHT_GRAY_DYED, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.CYAN_FABRIC, ConventionalBlockTags.CYAN_DYED, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.PURPLE_FABRIC, ConventionalBlockTags.PURPLE_DYED, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.BLUE_FABRIC, ConventionalBlockTags.BLUE_DYED, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.BROWN_FABRIC, ConventionalBlockTags.BROWN_DYED, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.GREEN_FABRIC, ConventionalBlockTags.GREEN_DYED, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.RED_FABRIC, ConventionalBlockTags.RED_DYED, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.BLACK_FABRIC, ConventionalBlockTags.BLACK_DYED, BlockTags.MINEABLE_WITH_PICKAXE);

        add(ModBlocks.AMALGAM_SLAB, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.SLABS);
        add(ModBlocks.AMALGAM_STAIRS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.STAIRS);
        add(ModBlocks.AMALGAM_ORE, ConventionalBlockTags.ORES, BlockTags.MINEABLE_WITH_PICKAXE);
        add(ModBlocks.AMALGAM_BLOCK, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.BEACON_BASE_BLOCKS);
        add(ModBlocks.AMALGAM_TRAPDOOR, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.TRAPDOORS);

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

    private void add(RegistrySupplier<Block> block, TagKey<Block>... objects) {
        var key = block.getKey();
        for (TagKey<Block> object : objects)
            tag(object).add(key);
    }

	private TagAppender<Block> add(TagKey<Block> tag, Object... objects) {
		var appender = tag(tag);

        for(var object : objects) {
            if(object instanceof RegistrySupplier<?> supplier) {
                if(supplier.get() instanceof Block block) {
                    appender.add(block.builtInRegistryHolder().key());
                }
            } else if (object instanceof Block block) {
                appender.add(block.builtInRegistryHolder().key());
            } else if (object instanceof TagKey<?> key && key.isFor(Registries.BLOCK)) {
                appender.addTag((TagKey<Block>) key);
            } else if (object instanceof Collection<?> list) {
                for(var element : list) {
                    if (element instanceof RegistrySupplier<?> supplier) {
                        if (supplier.get() instanceof Block block) {
                            appender.add(block.builtInRegistryHolder().key());
                        }
                    } else if (element instanceof Block block) {
                        appender.add(block.builtInRegistryHolder().key());
                    }
                }
            } else if (object instanceof ModBlocks.DecayGroupSet set) {
                set.fence().unwrapKey().ifPresent(appender::add);
                set.gate().unwrapKey().ifPresent(appender::add);
                set.button().unwrapKey().ifPresent(appender::add);
                set.slab().unwrapKey().ifPresent(appender::add);
                set.stairs().unwrapKey().ifPresent(appender::add);
                set.wall().unwrapKey().ifPresent(appender::add);
            }
        }


		return appender;
	}
}
