package org.dimdev.dimdoors.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.tag.ModBlockTags;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
	public BlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider arg) {
		tag(ModBlockTags.DECAY_TO_AIR).add(
				reverseLookup(Blocks.COBWEB),
				reverseLookup(ModBlocks.DRIFTWOOD_LEAVES),
				reverseLookup(ModBlocks.DRIFTWOOD_SAPLING),
				reverseLookup(Blocks.GLASS_PANE),
				reverseLookup(Blocks.MOSS_CARPET),
				reverseLookup(ModBlocks.DRIFTWOOD_TRAPDOOR),
				reverseLookup(Blocks.RAIL),
				reverseLookup(ModBlocks.RUST),
				reverseLookup(ModBlocks.UNRAVELED_SPIKE));
		tag(ModBlockTags.DECAY_TO_RAIL).add(
				reverseLookup(Blocks.ACTIVATOR_RAIL),
				reverseLookup(Blocks.DETECTOR_RAIL),
				reverseLookup(Blocks.POWERED_RAIL));
		tag(ModBlockTags.DECAY_TO_GRITTY_STONE).add(
				reverseLookup(Blocks.INFESTED_STONE),
				reverseLookup(Blocks.INFESTED_COBBLESTONE),
				reverseLookup(Blocks.INFESTED_STONE_BRICKS),
				reverseLookup(Blocks.INFESTED_MOSSY_STONE_BRICKS),
				reverseLookup(Blocks.INFESTED_CRACKED_STONE_BRICKS),
				reverseLookup(Blocks.INFESTED_CHISELED_STONE_BRICKS));
		tag(ModBlockTags.DECAY_TO_SOLID_STATIC).add(
				reverseLookup(Blocks.BEDROCK),
				reverseLookup(Blocks.END_PORTAL_FRAME),
				reverseLookup(Blocks.COMMAND_BLOCK),
				reverseLookup(Blocks.CHAIN_COMMAND_BLOCK),
				reverseLookup(Blocks.REPEATING_COMMAND_BLOCK)
		);
		tag(ModBlockTags.DECAY_UNRAVELED_FENCE).add(
				reverseLookup(ModBlocks.CLAY_FENCE),
				reverseLookup(ModBlocks.DARK_SAND_FENCE)
		);
		tag(ModBlockTags.DECAY_UNRAVELED_GATE).add(
				reverseLookup(ModBlocks.CLAY_GATE),
				reverseLookup(ModBlocks.DARK_SAND_GATE)
		);
		tag(ModBlockTags.DECAY_UNRAVELED_BUTTON).add(
				reverseLookup(ModBlocks.CLAY_BUTTON),
				reverseLookup(ModBlocks.DARK_SAND_BUTTON)
		);
		tag(ModBlockTags.DECAY_UNRAVELED_SLAB).add(
				reverseLookup(ModBlocks.CLAY_SLAB),
				reverseLookup(ModBlocks.DARK_SAND_SLAB)
		);
		tag(ModBlockTags.DECAY_UNRAVELED_STAIRS).add(
				reverseLookup(ModBlocks.CLAY_STAIRS),
				reverseLookup(ModBlocks.DARK_SAND_STAIRS)
		);
		tag(ModBlockTags.DECAY_TO_GLASS_PANE).add(
				reverseLookup(Blocks.GRAY_STAINED_GLASS_PANE),
				reverseLookup(Blocks.BLACK_STAINED_GLASS_PANE),
				reverseLookup(Blocks.ORANGE_STAINED_GLASS_PANE),
				reverseLookup(Blocks.BLUE_STAINED_GLASS_PANE),
				reverseLookup(Blocks.BROWN_STAINED_GLASS_PANE),
				reverseLookup(Blocks.CYAN_STAINED_GLASS_PANE),
				reverseLookup(Blocks.GREEN_STAINED_GLASS_PANE),
				reverseLookup(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE),
				reverseLookup(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE),
				reverseLookup(Blocks.LIME_STAINED_GLASS_PANE),
				reverseLookup(Blocks.MAGENTA_STAINED_GLASS_PANE),
				reverseLookup(Blocks.PINK_STAINED_GLASS_PANE),
				reverseLookup(Blocks.PURPLE_STAINED_GLASS_PANE),
				reverseLookup(Blocks.RED_STAINED_GLASS_PANE),
				reverseLookup(Blocks.WHITE_STAINED_GLASS_PANE),
				reverseLookup(Blocks.YELLOW_STAINED_GLASS_PANE)
		);
		tag(ModBlockTags.DECAY_TO_RUST).add(
				//REDSTONE VARIANTS
				reverseLookup(Blocks.LIGHTNING_ROD),
				reverseLookup(Blocks.LANTERN),
				reverseLookup(Blocks.IRON_BARS),
				reverseLookup(Blocks.HOPPER),
				reverseLookup(Blocks.CHAIN),
				reverseLookup(Blocks.CAULDRON)
		);
		tag(ModBlockTags.DECAY_TO_UNRAVELED_SPIKE).add(
				reverseLookup(Blocks.END_ROD),
				reverseLookup(Blocks.POINTED_DRIPSTONE)
		).addOptionalTag(BlockTags.FLOWER_POTS.location()).addOptionalTag(BlockTags.CANDLES.location());
		tag(ModBlockTags.DECAY_TO_WITHER_ROSE).addOptionalTag(BlockTags.SMALL_FLOWERS.location()).addOptionalTag(BlockTags.TALL_FLOWERS.location());
		tag(ModBlockTags.DECAY_TO_CLAY);
		tag(ModBlockTags.DECAY_TO_CLAY);
		tag(ModBlockTags.DECAY_CLAY_FENCE);
		tag(ModBlockTags.DECAY_CLAY_GATE);
		tag(ModBlockTags.DECAY_CLAY_BUTTON);
		tag(ModBlockTags.DECAY_CLAY_SLAB);
		tag(ModBlockTags.DECAY_CLAY_STAIRS);
		tag(ModBlockTags.DECAY_TO_DARK_SAND);
		tag(ModBlockTags.DECAY_DARK_SAND_FENCE);
		tag(ModBlockTags.DECAY_DARK_SAND_GATE);
		tag(ModBlockTags.DECAY_DARK_SAND_BUTTON);
		tag(ModBlockTags.DECAY_DARK_SAND_SLAB);
		tag(ModBlockTags.DECAY_DARK_SAND_STAIRS);
		tag(ModBlockTags.DECAY_TO_AMALGAM);
	}
}
