package org.dimdev.dimdoors.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.tag.ModBlockTags;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
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

		add(ModBlockTags.DECAYS_TO_AIR,
				Blocks.COBWEB,
                Blocks.SUGAR_CANE,
                ModBlocks.DRIFTWOOD_LEAVES,
                ModBlocks.DRIFTWOOD_SAPLING,
                ModBlocks.SAND_LAYER,
                ModBlockTags.MINOR_PLANTS,
                Blocks.SUGAR_CANE,
				Blocks.MOSS_CARPET,
				ModBlocks.RUST,
				Blocks.WITHER_ROSE);

        add(ModBlockTags.DECAYS_TO_MOSS_CARPET,
                BlockTags.WOOL_CARPETS,
                Blocks.GLOW_LICHEN,
                Blocks.VINE,
                BlockTags.CAVE_VINES,
                ModBlocks.DRIFTWOOD_TRAPDOOR);

        add(ModBlockTags.DECAYS_TO_RUST,
                Blocks.RAIL,
                Blocks.REDSTONE_BLOCK,
                Blocks.REDSTONE_LAMP,
                Blocks.REDSTONE_TORCH,
                Blocks.REDSTONE_WIRE,
                Blocks.REDSTONE_WALL_TORCH,
                Blocks.LIGHTNING_ROD,
                Blocks.LANTERN,
                Blocks.IRON_TRAPDOOR,
                Blocks.IRON_BARS,
                Blocks.HOPPER,
                Blocks.CHAIN,
                BlockTags.CAULDRONS,
                Blocks.BELL
        );

		add(ModBlockTags.DECAYS_TO_RAIL,
				Blocks.ACTIVATOR_RAIL,
				Blocks.DETECTOR_RAIL,
				Blocks.POWERED_RAIL);
		add(ModBlockTags.DECAYS_TO_GRITTY_STONE,
				Blocks.INFESTED_STONE,
				Blocks.INFESTED_COBBLESTONE,
				Blocks.INFESTED_STONE_BRICKS,
				Blocks.INFESTED_MOSSY_STONE_BRICKS,
				Blocks.INFESTED_CRACKED_STONE_BRICKS,
				Blocks.INFESTED_CHISELED_STONE_BRICKS);
		add(ModBlockTags.DECAYS_TO_SOLID_STATIC,
				Blocks.BEDROCK,
				Blocks.END_PORTAL_FRAME,
				Blocks.COMMAND_BLOCK,
				Blocks.CHAIN_COMMAND_BLOCK,
				Blocks.REPEATING_COMMAND_BLOCK
		);
		add(ModBlockTags.DECAYS_TO_UNRAVELED_FENCE,
				ModBlocks.CLAY_FENCE.get(),
				ModBlocks.DARK_SAND_FENCE.get()
		);

        add(ModBlockTags.DECAYS_TO_UNRAVELED_GATE,
                ModBlocks.CLAY_GATE
        );


		add(ModBlockTags.DECAYS_TO_UNRAVELED_BUTTON,
				ModBlocks.CLAY_BUTTON.get(),
				ModBlocks.DARK_SAND_BUTTON.get()
		);

        		add(ModBlockTags.DECAYS_TO_UNRAVELED_SLAB,
				ModBlocks.CLAY_SLAB.get(),
				ModBlocks.DARK_SAND_SLAB.get()
		);
		add(ModBlockTags.DECAYS_TO_UNRAVELED_STAIRS,
				ModBlocks.CLAY_STAIRS.get(),
				ModBlocks.DARK_SAND_STAIRS.get()
		);
		add(ModBlockTags.DECAYS_TO_TO_GLASS_PANE,
				Blocks.GRAY_STAINED_GLASS_PANE,
				Blocks.BLACK_STAINED_GLASS_PANE,
				Blocks.ORANGE_STAINED_GLASS_PANE,
				Blocks.BLUE_STAINED_GLASS_PANE,
				Blocks.BROWN_STAINED_GLASS_PANE,
				Blocks.CYAN_STAINED_GLASS_PANE,
				Blocks.GREEN_STAINED_GLASS_PANE,
				Blocks.LIGHT_BLUE_STAINED_GLASS_PANE,
				Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
				Blocks.LIME_STAINED_GLASS_PANE,
				Blocks.MAGENTA_STAINED_GLASS_PANE,
				Blocks.PINK_STAINED_GLASS_PANE,
				Blocks.PURPLE_STAINED_GLASS_PANE,
				Blocks.RED_STAINED_GLASS_PANE,
				Blocks.WHITE_STAINED_GLASS_PANE,
				Blocks.YELLOW_STAINED_GLASS_PANE
		);
		add(ModBlockTags.DECAYS_TO_RUST,
				//REDSTONE VARIANTS
				Blocks.LIGHTNING_ROD,
				Blocks.LANTERN,
				Blocks.IRON_BARS,
				Blocks.HOPPER,
				Blocks.CHAIN,
				Blocks.CAULDRON,
				Blocks.BELL
		);
		add(ModBlockTags.DECAYS_TO_UNRAVELED_SPIKE,
				Blocks.END_ROD,
				Blocks.POINTED_DRIPSTONE
		).addOptionalTag(BlockTags.FLOWER_POTS.location()).addOptionalTag(BlockTags.CANDLES.location());
		tag(ModBlockTags.DECAYS_TO_WITHER_ROSE).addOptionalTag(BlockTags.SMALL_FLOWERS.location()).addOptionalTag(BlockTags.TALL_FLOWERS.location());
		add(ModBlockTags.DECAYS_TO_CLAY,
				ModBlocks.AMALGAM_BLOCK.get(),
				Blocks.MUD,
				Blocks.TERRACOTTA,
				Blocks.BRICKS
		);
		add(ModBlockTags.DECAYS_TO_CLAY_FENCE,
				ModBlocks.CLAY_FENCE.get(),
				ModBlocks.MUD_FENCE.get()
		);
		add(ModBlockTags.DECAYS_TO_CLAY_GATE,
				ModBlocks.CLAY_GATE.get(),
				ModBlocks.MUD_GATE.get()
		);

        add(ModBlockTags.DECAYS_TO_CLAY_WALL,
                Blocks.BRICK_WALL
        );

		add(ModBlockTags.DECAYS_TO_CLAY_BUTTON,
				ModBlocks.CLAY_BUTTON.get(),
				ModBlocks.MUD_BUTTON.get()
		);
		add(ModBlockTags.DECAYS_TO_CLAY_SLAB,
				Blocks.BRICK_SLAB,
				ModBlocks.MUD_SLAB.get(),
				ModBlocks.AMALGAM_SLAB.get()
		);
		add(ModBlockTags.DECAYS_TO_CLAY_STAIRS,
				Blocks.BRICK_STAIRS,
				ModBlocks.MUD_STAIRS.get(),
				ModBlocks.AMALGAM_STAIRS.get()
		);

        add(ModBlockTags.DECAYS_TO_DARK_SAND,
				Blocks.AMETHYST_BLOCK,
				Blocks.GLASS,
				Blocks.GRAVEL,
				Blocks.RED_SAND,
				Blocks.SAND,
				Blocks.SOUL_SAND
		);

        add(ModBlockTags.DECAYS_TO_DARK_SAND_FENCE,
                ModBlocks.GRAVEL_FENCE
        );


		add(ModBlockTags.DECAYS_TO_UNRAVELED_FABRIC,
				ModBlocks.DARK_SAND.get(),
				Blocks.CLAY);

		add(ModBlockTags.DECAYS_TO_MUD,
				Blocks.DIRT,
				Blocks.GRASS_BLOCK,
				Blocks.PODZOL,
				Blocks.MYCELIUM,
				ModBlocks.DRIFTWOOD_PLANKS.get(),
				Blocks.COAL_BLOCK,
				Blocks.COMPOSTER,
				Blocks.CHEST,
				Blocks.BONE_BLOCK,
				Blocks.SKELETON_SKULL,
				Blocks.SKELETON_WALL_SKULL,
				Blocks.WITHER_SKELETON_SKULL,
				Blocks.WITHER_SKELETON_WALL_SKULL,
				Blocks.DRAGON_HEAD,
				Blocks.DRAGON_WALL_HEAD,
				Blocks.CACTUS,
				Blocks.COCOA,
				Blocks.PUMPKIN,
				Blocks.MELON,
				Blocks.HAY_BLOCK,
				Blocks.MOSS_BLOCK,
				Blocks.SLIME_BLOCK,
				Blocks.HONEYCOMB_BLOCK,
				Blocks.LECTERN,
				Blocks.PURPUR_BLOCK,
				Blocks.DRIED_KELP_BLOCK,
				Blocks.NETHER_WART_BLOCK,
				Blocks.PACKED_MUD);

		add(ModBlockTags.DECAYS_TO_NETHERWART_BLOCK,
				Blocks.BROWN_MUSHROOM_BLOCK,
				Blocks.RED_MUSHROOM_BLOCK);

		add(ModBlockTags.DECAYS_TO_GLASS,
				Blocks.TINTED_GLASS,
				Blocks.REDSTONE_BLOCK,
				Blocks.GRAY_STAINED_GLASS,
				Blocks.BLACK_STAINED_GLASS,
				Blocks.ORANGE_STAINED_GLASS,
				Blocks.BLUE_STAINED_GLASS,
				Blocks.BROWN_STAINED_GLASS,
				Blocks.CYAN_STAINED_GLASS,
				Blocks.GREEN_STAINED_GLASS,
				Blocks.LIGHT_BLUE_STAINED_GLASS,
				Blocks.LIGHT_GRAY_STAINED_GLASS,
				Blocks.LIME_STAINED_GLASS,
				Blocks.MAGENTA_STAINED_GLASS,
				Blocks.PINK_STAINED_GLASS,
				Blocks.PURPLE_STAINED_GLASS,
				Blocks.RED_STAINED_GLASS,
				Blocks.WHITE_STAINED_GLASS,
				Blocks.YELLOW_STAINED_GLASS);

		add(ModBlockTags.DECAYS_TO_GRAVEL,
				ModBlocks.AMALGAM_BLOCK.get(),
				ModBlocks.CLOD_ORE.get(),
				Blocks.COBBLESTONE);

        add(ModBlockTags.DECAYS_TO_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);

		add(ModBlockTags.DECAYS_TO_AMALGAM_ORE, Blocks.RAW_COPPER_BLOCK, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.RAW_IRON_BLOCK, Blocks.DEEPSLATE_IRON_ORE, Blocks.IRON_ORE, Blocks.RAW_GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.NETHER_GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE);

		add(ModBlockTags.DECAYS_TO_CLOD_ORE, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE, Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.NETHER_QUARTZ_ORE);

		add(ModBlockTags.DECAYS_TO_COBBLESTONE,
				Blocks.ANDESITE,
				Blocks.BASALT,
				Blocks.BLACKSTONE,
				Blocks.CALCITE,
				Blocks.DEEPSLATE,
				Blocks.DIORITE,
				Blocks.DRIPSTONE_BLOCK,
				Blocks.END_STONE,
				Blocks.FURNACE,
				Blocks.GRANITE,
				Blocks.NETHERRACK,
				Blocks.PRISMARINE,
				Blocks.STONE,
				Blocks.TUFF);

		add(ModBlockTags.DECAYS_TO_COBBLESTONE_SLAB, Blocks.STONE_SLAB, Blocks.STONECUTTER);

		add(ModBlockTags.DECAYS_TO_STONE, ModBlocks.CLOD_BLOCK.get(), Blocks.CRACKED_STONE_BRICKS, Blocks.GLOWSTONE, Blocks.OBSIDIAN, Blocks.REDSTONE_BLOCK);

		tag(ModBlockTags.DECAYS_TO_DARK_SAND_SLAB);
		tag(ModBlockTags.DECAYS_TO_DARK_SAND_STAIRS);
		tag(ModBlockTags.DECAYS_TO_DARK_SAND_WALL);
		add(ModBlockTags.DECAYS_TO_AMALGAM,
                Blocks.IRON_BLOCK,
				Blocks.COPPER_BLOCK,
				Blocks.CUT_COPPER,
				Blocks.GOLD_BLOCK);


		add(ModBlockTags.DECAYS_TO_DRIFTWOOD_PLANK).addOptionalTag(BlockTags.PLANKS.location());

		add(BlockTags.FENCES).add(
				ModBlocks.CLAY_FENCE.getKey(),
				ModBlocks.GRAVEL_FENCE.getKey(),
				ModBlocks.MUD_FENCE.getKey(),
				ModBlocks.UNRAVELED_FENCE.getKey(),
				ModBlocks.NETHERRACK_FENCE.getKey(),
				ModBlocks.DARK_SAND_FENCE.getKey());

		add(BlockTags.WALLS).add(
				ModBlocks.CLAY_WALL.getKey(),
				ModBlocks.DARK_SAND_WALL.getKey(),
				ModBlocks.DEEPSLATE_WALL.getKey(),
				ModBlocks.GRAVEL_WALL.getKey(),
				ModBlocks.NETHERRACK_WALL.getKey(),
				ModBlocks.END_STONE_WALL.getKey(),
				ModBlocks.RED_SAND_WALL.getKey(),
				ModBlocks.SAND_WALL.getKey()
		);

        add(BlockTags.LOGS, ModBlocks.DRIFTWOOD_LOG);
        add(BlockTags.PLANKS, ModBlocks.DRIFTWOOD_PLANKS);
        add(BlockTags.LEAVES, ModBlocks.DRIFTWOOD_LEAVES);
        add(BlockTags.SAPLINGS, ModBlocks.DRIFTWOOD_SAPLING);
        add(BlockTags.WOODEN_FENCES, ModBlocks.DRIFTWOOD_FENCE);
        add(BlockTags.WOODEN_BUTTONS, ModBlocks.DRIFTWOOD_BUTTON);
        add(BlockTags.WOODEN_SLABS, ModBlocks.DRIFTWOOD_SLAB);
        add(BlockTags.WOODEN_STAIRS, ModBlocks.DRIFTWOOD_STAIRS);
        add(BlockTags.WOODEN_DOORS, ModBlocks.DRIFTWOOD_DOOR);
        add(BlockTags.WOODEN_TRAPDOORS, ModBlocks.DRIFTWOOD_TRAPDOOR);

        add(BlockTags.TRAPDOORS, ModBlocks.AMALGAM_TRAPDOOR);
        add(BlockTags.SLABS,
                ModBlocks.AMALGAM_SLAB,
                ModBlocks.SAND_SLAB,
                ModBlocks.GRAVEL_SLAB,
                ModBlocks.DARK_SAND_SLAB,
                ModBlocks.CLAY_SLAB,
                ModBlocks.MUD_SLAB,
                ModBlocks.UNRAVELED_SLAB,
                ModBlocks.DEEPSLATE_SLAB,
                ModBlocks.SAND_SLAB,
                ModBlocks.END_STONE_STAIRS,
                ModBlocks.NETHERRACK_SLAB
        );

        add(BlockTags.FENCES,
                ModBlocks.CLAY_GATE,
                ModBlocks.MUD_GATE,
                ModBlocks.UNRAVELED_GATE
        );

        add(BlockTags.STAIRS,
                ModBlocks.GRAVEL_STAIRS,
                ModBlocks.DARK_SAND_STAIRS,
                ModBlocks.CLAY_STAIRS,
                ModBlocks.MUD_STAIRS,
                ModBlocks.UNRAVELED_STAIRS,
                ModBlocks.DEEPSLATE_STAIRS,
                ModBlocks.RED_SAND_STAIRS,
                ModBlocks.SAND_STAIRS,
                ModBlocks.END_STONE_STAIRS,
                ModBlocks.NETHERRACK_STAIRS
        );



        add(BlockTags.MINEABLE_WITH_PICKAXE,
                ModBlocks.GOLD_DOOR,
                ModBlocks.AMALGAM_BLOCK,
                ModBlocks.REALITY_SPONGE,
                ModBlocks.GOLD_DOOR,
                ModBlocks.QUARTZ_DOOR,
                ModBlocks.WHITE_FABRIC,
                ModBlocks.ORANGE_FABRIC,
                ModBlocks.MAGENTA_FABRIC,
                ModBlocks.LIGHT_BLUE_FABRIC,
                ModBlocks.YELLOW_FABRIC,
                ModBlocks.LIME_FABRIC,
                ModBlocks.PINK_FABRIC,
                ModBlocks.GRAY_FABRIC,
                ModBlocks.LIGHT_GRAY_FABRIC,
                ModBlocks.CYAN_FABRIC,
                ModBlocks.PURPLE_FABRIC,
                ModBlocks.BLUE_FABRIC,
                ModBlocks.BROWN_FABRIC,
                ModBlocks.GREEN_FABRIC,
                ModBlocks.RED_FABRIC,
                ModBlocks.BLACK_FABRIC,
                ModBlocks.UNRAVELLED_FABRIC,
                ModBlocks.AMALGAM_BLOCK,
                ModBlocks.AMALGAM_DOOR,
                ModBlocks.AMALGAM_TRAPDOOR,
                ModBlocks.RUST,
                ModBlocks.AMALGAM_SLAB,
                ModBlocks.AMALGAM_STAIRS,
                ModBlocks.AMALGAM_ORE,
                ModBlocks.CLOD_ORE,
                ModBlocks.CLOD_BLOCK,
                ModBlocks.UNRAVELED_FENCE,
                ModBlocks.UNRAVELED_GATE,
                ModBlocks.UNRAVELED_BUTTON,
                ModBlocks.UNRAVELED_SLAB,
                ModBlocks.UNRAVELED_STAIRS,
                ModBlocks.DEEPSLATE_SLAB,
                ModBlocks.DEEPSLATE_STAIRS,
                ModBlocks.DEEPSLATE_WALL,
                ModBlocks.END_STONE_SLAB,
                ModBlocks.END_STONE_STAIRS,
                ModBlocks.END_STONE_WALL,
                ModBlocks.NETHERRACK_FENCE,
                ModBlocks.NETHERRACK_SLAB,
                ModBlocks.NETHERRACK_STAIRS,
                ModBlocks.NETHERRACK_WALL,
                ModBlocks.UNRAVELED_SPIKE,
                ModBlocks.GRITTY_STONE
        );

        add(BlockTags.MINEABLE_WITH_AXE,
                ModBlocks.DRIFTWOOD_WOOD,
                ModBlocks.DRIFTWOOD_LOG,
                ModBlocks.DRIFTWOOD_PLANKS,
                ModBlocks.DRIFTWOOD_FENCE,
                ModBlocks.DRIFTWOOD_GATE,
                ModBlocks.DRIFTWOOD_BUTTON,
                ModBlocks.DRIFTWOOD_SLAB,
                ModBlocks.DRIFTWOOD_STAIRS,
                ModBlocks.DRIFTWOOD_DOOR,
                ModBlocks.DRIFTWOOD_TRAPDOOR
        );

        add(BlockTags.MINEABLE_WITH_SHOVEL,
                ModBlocks.GRAVEL_FENCE,
                ModBlocks.GRAVEL_BUTTON,
                ModBlocks.GRAVEL_SLAB,
                ModBlocks.GRAVEL_STAIRS,
                ModBlocks.GRAVEL_WALL,
                ModBlocks.DARK_SAND,
                ModBlocks.DARK_SAND_FENCE,
                ModBlocks.DARK_SAND_BUTTON,
                ModBlocks.DARK_SAND_SLAB,
                ModBlocks.DARK_SAND_STAIRS,
                ModBlocks.DARK_SAND_WALL,
                ModBlocks.CLAY_FENCE,
                ModBlocks.CLAY_GATE,
                ModBlocks.CLAY_BUTTON,
                ModBlocks.CLAY_SLAB,
                ModBlocks.CLAY_STAIRS,
                ModBlocks.CLAY_WALL,
                ModBlocks.MUD_FENCE,
                ModBlocks.MUD_GATE,
                ModBlocks.MUD_BUTTON,
                ModBlocks.MUD_SLAB,
                ModBlocks.MUD_STAIRS,
                ModBlocks.RED_SAND_SLAB,
                ModBlocks.RED_SAND_STAIRS,
                ModBlocks.RED_SAND_WALL,
                ModBlocks.SAND_SLAB,
                ModBlocks.SAND_STAIRS,
                ModBlocks.SAND_WALL
        );
	}

	private TagAppender<Block> add(TagKey<Block> tag, Object... blocks) {
		var appender = tag(tag);

        Stream.of(blocks).map(a -> a instanceof RegistrySupplier<?> supplier ? supplier.get() instanceof Block block ? block : a instanceof Block block ? block : null : null).filter(Objects::nonNull).map(Block::builtInRegistryHolder).map(Holder.Reference::key).forEach(appender::add);
		return appender;
	}
}
