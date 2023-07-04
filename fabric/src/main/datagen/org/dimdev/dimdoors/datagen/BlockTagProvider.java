package org.dimdev.dimdoors.datagen;

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
		add(ModBlockTags.DECAY_TO_AIR,
				Blocks.COBWEB,
				ModBlocks.DRIFTWOOD_LEAVES.get(),
				ModBlocks.DRIFTWOOD_SAPLING.get(),
				Blocks.GLASS_PANE,
				Blocks.MOSS_CARPET,
				ModBlocks.DRIFTWOOD_TRAPDOOR.get(),
				Blocks.RAIL,
				ModBlocks.RUST.get(),
				ModBlocks.UNRAVELED_SPIKE.get());
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
		add(ModBlockTags.DECAY_UNRAVELED_GATE,
				ModBlocks.CLAY_GATE.get(),
				ModBlocks.DARK_SAND_GATE.get()
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
		).addTag(BlockTags.FLOWER_POTS).addTag(BlockTags.CANDLES);
		tag(ModBlockTags.DECAY_TO_WITHER_ROSE).addTag(BlockTags.SMALL_FLOWERS).addTag(BlockTags.TALL_FLOWERS);
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
				ModBlocks.CLAY_SLAB.get(),
				ModBlocks.MUD_SLAB.get()
		);
		add(ModBlockTags.DECAY_CLAY_STAIRS,
				ModBlocks.CLAY_STAIRS.get(),
				ModBlocks.MUD_STAIRS.get()
		);
		add(ModBlockTags.DECAY_TO_DARK_SAND,
				Blocks.AMETHYST_BLOCK,
				Blocks.GLASS,
				Blocks.GRAVEL,
				Blocks.RED_SAND,
				Blocks.SAND,
				Blocks.SOUL_SAND
		);
//		tag(ModBlockTags.DECAY_DARK_SAND_SLAB);
//		tag(ModBlockTags.DECAY_DARK_SAND_STAIRS);
//		tag(ModBlockTags.DECAY_DARK_SAND_WALL);
//		tag(ModBlockTags.DECAY_TO_AMALGAM);
	}

	private TagAppender<Block> add(TagKey<Block> tag, Block... blocks) {
		var appender = tag(tag);
		Stream.of(blocks).map(Block::builtInRegistryHolder).map(Holder.Reference::key).forEach(appender::add);
		return appender;
	}
}
