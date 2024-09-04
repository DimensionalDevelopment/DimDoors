package org.dimdev.dimdoors.datagen;

<<<<<<< HEAD
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
=======
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
>>>>>>> merge-branch
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.tag.ModBlockTags;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
<<<<<<< HEAD
	public BlockTagProvider(FabricDataGenerator output) {
		super(output);
=======
	public BlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
>>>>>>> merge-branch
	}

	@Override
	protected void generateTags() {
	add(ModBlockTags.DECAY_TO_AIR,
		Blocks.COBWEB,
		ModBlocks.DRIFTWOOD_LEAVES.get(),
				ModBlocks.DRIFTWOOD_SAPLING.get(),
				Blocks.GLASS_PANE,
				Blocks.MOSS_CARPET,
				ModBlocks.DRIFTWOOD_TRAPDOOR.get(),
				Blocks.RAIL,
				ModBlocks.RUST.get(),
				ModBlocks.UNRAVELED_SPIKE.get(),
				Blocks.WITHER_ROSE);
		add(ModBlockTags.DECAY_TO_RAIL,
				Blocks.ACTIVATOR_RAIL,
				Blocks.DETECTOR_RAIL,
				Blocks.POWERED_RAIL);
		add(ModBlockTags.DECAY_TO_GRITTY_STONE,
				Blocks.INFESTED_STONE,
				Blocks.INFESTED_COBBLESTONE,
				Blocks.INFESTED_STONE_BRICKS,
				Blocks.INFESTED_MOSSY_STONE_BRICKS,
				Blocks.INFESTED_CRACKED_STONE_BRICKS,
				Blocks.INFESTED_CHISELED_STONE_BRICKS);
		add(ModBlockTags.DECAY_TO_SOLID_STATIC,
				Blocks.BEDROCK,
				Blocks.END_PORTAL_FRAME,
				Blocks.COMMAND_BLOCK,
				Blocks.CHAIN_COMMAND_BLOCK,
				Blocks.REPEATING_COMMAND_BLOCK
		);
		add(ModBlockTags.DECAY_UNRAVELED_FENCE,
				ModBlocks.CLAY_FENCE.get(),
				ModBlocks.DARK_SAND_FENCE.get()
		);

		add(ModBlockTags.DECAY_UNRAVELED_BUTTON,
				ModBlocks.CLAY_BUTTON.get(),
				ModBlocks.DARK_SAND_BUTTON.get()
		);
		add(ModBlockTags.DECAY_UNRAVELED_SLAB,
				ModBlocks.CLAY_SLAB.get(),
				ModBlocks.DARK_SAND_SLAB.get()
		);
		add(ModBlockTags.DECAY_UNRAVELED_STAIRS,
				ModBlocks.CLAY_STAIRS.get(),
				ModBlocks.DARK_SAND_STAIRS.get()
		);
		add(ModBlockTags.DECAY_TO_GLASS_PANE,
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
		add(ModBlockTags.DECAY_TO_RUST,
				//REDSTONE VARIANTS
				Blocks.LIGHTNING_ROD,
				Blocks.LANTERN,
				Blocks.IRON_BARS,
				Blocks.HOPPER,
				Blocks.CHAIN,
				Blocks.CAULDRON,
				Blocks.BELL
		);
		add(ModBlockTags.DECAY_TO_UNRAVELED_SPIKE,
				Blocks.END_ROD,
				Blocks.POINTED_DRIPSTONE
		).addOptionalTag(BlockTags.FLOWER_POTS.location()).addOptionalTag(BlockTags.CANDLES.location());
		tag(ModBlockTags.DECAY_TO_WITHER_ROSE).addOptionalTag(BlockTags.SMALL_FLOWERS.location()).addOptionalTag(BlockTags.TALL_FLOWERS.location());
		add(ModBlockTags.DECAY_TO_CLAY,
				ModBlocks.AMALGAM_BLOCK.get(),
				Blocks.MUD,
				Blocks.TERRACOTTA,
				Blocks.BRICKS
		);
		add(ModBlockTags.DECAY_CLAY_FENCE,
				ModBlocks.CLAY_FENCE.get(),
				ModBlocks.MUD_FENCE.get()
		);
		add(ModBlockTags.DECAY_CLAY_GATE,
				ModBlocks.CLAY_GATE.get(),
				ModBlocks.MUD_GATE.get()
		);
		add(ModBlockTags.DECAY_CLAY_BUTTON,
				ModBlocks.CLAY_BUTTON.get(),
				ModBlocks.MUD_BUTTON.get()
		);
		add(ModBlockTags.DECAY_CLAY_SLAB,
				Blocks.BRICK_SLAB,
				ModBlocks.MUD_SLAB.get(),
				ModBlocks.AMALGAM_SLAB.get()
		);
		add(ModBlockTags.DECAY_CLAY_STAIRS,
				Blocks.BRICK_STAIRS,
				ModBlocks.MUD_STAIRS.get(),
				ModBlocks.AMALGAM_STAIRS.get()
		);
		add(ModBlockTags.DECAY_TO_DARK_SAND,
				Blocks.AMETHYST_BLOCK,
				Blocks.GLASS,
				Blocks.GRAVEL,
				Blocks.RED_SAND,
				Blocks.SAND,
				Blocks.SOUL_SAND
		);

		add(ModBlockTags.DECAY_TO_UNRAVELED_FABRIC,
				ModBlocks.DARK_SAND.get(),
				Blocks.CLAY);

		add(ModBlockTags.DECAY_TO_MUD,
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

		add(ModBlockTags.DECAY_TO_NETHERWART_BLOCK,
				Blocks.BROWN_MUSHROOM_BLOCK,
				Blocks.RED_MUSHROOM_BLOCK);

		add(ModBlockTags.DECAY_TO_GLASS,
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

		add(ModBlockTags.DECAY_TO_GRAVEL,
				ModBlocks.AMALGAM_BLOCK.get(),
				ModBlocks.CLOD_ORE.get(),
				Blocks.COBBLESTONE);

		add(ModBlockTags.DECAY_TO_AMALGAM_ORE, Blocks.RAW_COPPER_BLOCK, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.RAW_IRON_BLOCK, Blocks.DEEPSLATE_IRON_ORE, Blocks.IRON_ORE, Blocks.RAW_GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.NETHER_GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE);

		add(ModBlockTags.DECAY_TO_CLOD_ORE, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE, Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.NETHER_QUARTZ_ORE);

		add(ModBlockTags.DECAY_TO_COBBLESTONE,
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

		add(ModBlockTags.DECAY_TO_COBBLESTONE_SLAB, Blocks.STONE_SLAB, Blocks.STONECUTTER);

		add(ModBlockTags.DECAY_TO_STONE, ModBlocks.CLOD_BLOCK.get(), Blocks.CRACKED_STONE_BRICKS, Blocks.GLOWSTONE, Blocks.OBSIDIAN, Blocks.REDSTONE_BLOCK);

//		tag(ModBlockTags.DECAY_DARK_SAND_SLAB);
//		tag(ModBlockTags.DECAY_DARK_SAND_STAIRS);
//		tag(ModBlockTags.DECAY_DARK_SAND_WALL);
		tag(ModBlockTags.DECAY_TO_AMALGAM)
				.add(Blocks.IRON_BLOCK.builtInRegistryHolder().key())
				.add(Blocks.COPPER_BLOCK.builtInRegistryHolder().key())
				.add(Blocks.CUT_COPPER.builtInRegistryHolder().key())
				.add(Blocks.GOLD_BLOCK.builtInRegistryHolder().key());


		add(ModBlockTags.DECAY_TO_DRIFTWOOD_PLANK).addOptionalTag(BlockTags.PLANKS.location());

		add(BlockTags.FENCES).add(
				ModBlocks.CLAY_FENCE.get(),
				ModBlocks.GRAVEL_FENCE.get(),
				ModBlocks.MUD_FENCE.get(),
				ModBlocks.UNRAVELED_FENCE.get(),
				ModBlocks.NETHERRACK_FENCE.get(),
				ModBlocks.DARK_SAND_FENCE.get());

		add(BlockTags.WOODEN_FENCES).add(ModBlocks.DRIFTWOOD_FENCE.get());
		add(BlockTags.WALLS).add(
				ModBlocks.CLAY_WALL.get(),
				ModBlocks.DARK_SAND_WALL.get(),
				ModBlocks.DEEPSLATE_WALL.get(),
				ModBlocks.GRAVEL_WALL.get(),
				ModBlocks.NETHERRACK_WALL.get(),
				ModBlocks.END_STONE_WALL.get(),
				ModBlocks.RED_SAND_WALL.get(),
				ModBlocks.SAND_WALL.get()
		);
	}

	private TagAppender<Block> add(TagKey<Block> tag, Block... blocks) {
		var appender = tag(tag);
		Stream.of(blocks).map(Block::builtInRegistryHolder).map(Holder.Reference::key).forEach(appender::add);
		return appender;
	}
}
