package org.dimdev.dimdoors.datagen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.tag.ModBlockTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider {
	
	
	public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, modId, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider arg) {
		configure(arg.asGetterLookup());
	}

	protected void configure(HolderGetter.Provider arg) {
		tag(ModBlockTags.DECAY_TO_AIR).add(
				Blocks.COBWEB,
				ModBlocks.DRIFTWOOD_LEAVES.get(),
				ModBlocks.DRIFTWOOD_SAPLING.get(),
				Blocks.GLASS_PANE,
				Blocks.MOSS_CARPET,
				ModBlocks.DRIFTWOOD_TRAPDOOR.get(),
				Blocks.RAIL,
				ModBlocks.RUST.get(),
				ModBlocks.UNRAVELED_SPIKE.get());
		tag(ModBlockTags.DECAY_TO_RAIL).add(
				Blocks.ACTIVATOR_RAIL,
				Blocks.DETECTOR_RAIL,
				Blocks.POWERED_RAIL);
		tag(ModBlockTags.DECAY_TO_GRITTY_STONE).add(
				Blocks.INFESTED_STONE,
				Blocks.INFESTED_COBBLESTONE,
				Blocks.INFESTED_STONE_BRICKS,
				Blocks.INFESTED_MOSSY_STONE_BRICKS,
				Blocks.INFESTED_CRACKED_STONE_BRICKS,
				Blocks.INFESTED_CHISELED_STONE_BRICKS);
		tag(ModBlockTags.DECAY_TO_SOLID_STATIC).add(
				Blocks.BEDROCK,
				Blocks.END_PORTAL_FRAME,
				Blocks.COMMAND_BLOCK,
				Blocks.CHAIN_COMMAND_BLOCK,
				Blocks.REPEATING_COMMAND_BLOCK
		);
		tag(ModBlockTags.DECAY_UNRAVELED_FENCE).add(
				ModBlocks.CLAY_FENCE.get(),
				ModBlocks.DARK_SAND_FENCE.get()
		);
		tag(ModBlockTags.DECAY_UNRAVELED_GATE).add(
				ModBlocks.CLAY_GATE.get(),
				ModBlocks.DARK_SAND_GATE.get()
		);
		tag(ModBlockTags.DECAY_UNRAVELED_BUTTON).add(
				ModBlocks.CLAY_BUTTON.get(),
				ModBlocks.DARK_SAND_BUTTON.get()
		);
		tag(ModBlockTags.DECAY_UNRAVELED_SLAB).add(
				ModBlocks.CLAY_SLAB.get(),
				ModBlocks.DARK_SAND_SLAB.get()
		);
		tag(ModBlockTags.DECAY_UNRAVELED_STAIRS).add(
				ModBlocks.CLAY_STAIRS.get(),
				ModBlocks.DARK_SAND_STAIRS.get()
		);
		tag(ModBlockTags.DECAY_TO_GLASS_PANE).add(
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
		tag(ModBlockTags.DECAY_TO_RUST).add(
				//REDSTONE VARIANTS
				Blocks.LIGHTNING_ROD,
				Blocks.LANTERN,
				Blocks.IRON_BARS,
				Blocks.HOPPER,
				Blocks.CHAIN,
				Blocks.CAULDRON,
				Blocks.BELL
		);
		tag(ModBlockTags.DECAY_TO_UNRAVELED_SPIKE).add(
				Blocks.END_ROD,
				Blocks.POINTED_DRIPSTONE
		).addTag(BlockTags.FLOWER_POTS).addTag(BlockTags.CANDLES);
		tag(ModBlockTags.DECAY_TO_WITHER_ROSE).addTag(BlockTags.SMALL_FLOWERS).addTag(BlockTags.TALL_FLOWERS);
		tag(ModBlockTags.DECAY_TO_CLAY).add(
				ModBlocks.AMALGAM_BLOCK.get(),
				Blocks.MUD,
				Blocks.TERRACOTTA,
				Blocks.BRICKS
		);
		tag(ModBlockTags.DECAY_CLAY_FENCE).add(
				ModBlocks.CLAY_FENCE.get(),
				ModBlocks.MUD_FENCE.get()
		);
		tag(ModBlockTags.DECAY_CLAY_GATE).add(
				ModBlocks.CLAY_GATE.get(),
				ModBlocks.MUD_GATE.get()
		);
		tag(ModBlockTags.DECAY_CLAY_BUTTON).add(
				ModBlocks.CLAY_BUTTON.get(),
				ModBlocks.MUD_BUTTON.get()
		);
		tag(ModBlockTags.DECAY_CLAY_SLAB).add(
				ModBlocks.CLAY_SLAB.get(),
				ModBlocks.MUD_SLAB.get()
		);
		tag(ModBlockTags.DECAY_CLAY_STAIRS).add(
				ModBlocks.CLAY_STAIRS.get(),
				ModBlocks.MUD_STAIRS.get()
		);
		tag(ModBlockTags.DECAY_TO_DARK_SAND).add(
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
}
