package org.dimdev.dimdoors.world.limbo;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

import net.minecraft.resource.ResourceManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.world.decay.DecayLoader;
import org.dimdev.dimdoors.world.decay.DecayPattern;
import org.jetbrains.annotations.NotNull;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import static net.minecraft.block.Blocks.ACACIA_LOG;
import static net.minecraft.block.Blocks.ACACIA_PLANKS;
import static net.minecraft.block.Blocks.ACACIA_WOOD;
import static net.minecraft.block.Blocks.ANDESITE;
import static net.minecraft.block.Blocks.BIRCH_LOG;
import static net.minecraft.block.Blocks.BIRCH_PLANKS;
import static net.minecraft.block.Blocks.BIRCH_WOOD;
import static net.minecraft.block.Blocks.BLACKSTONE;
import static net.minecraft.block.Blocks.COAL_BLOCK;
import static net.minecraft.block.Blocks.COAL_ORE;
import static net.minecraft.block.Blocks.COBBLESTONE;
import static net.minecraft.block.Blocks.CRACKED_STONE_BRICKS;
import static net.minecraft.block.Blocks.DARK_OAK_LOG;
import static net.minecraft.block.Blocks.DARK_OAK_PLANKS;
import static net.minecraft.block.Blocks.DARK_OAK_WOOD;
import static net.minecraft.block.Blocks.DIORITE;
import static net.minecraft.block.Blocks.DIRT;
import static net.minecraft.block.Blocks.DIRT_PATH;
import static net.minecraft.block.Blocks.EMERALD_BLOCK;
import static net.minecraft.block.Blocks.EMERALD_ORE;
import static net.minecraft.block.Blocks.END_STONE;
import static net.minecraft.block.Blocks.END_STONE_BRICKS;
import static net.minecraft.block.Blocks.FARMLAND;
import static net.minecraft.block.Blocks.GLASS;
import static net.minecraft.block.Blocks.GOLD_BLOCK;
import static net.minecraft.block.Blocks.GOLD_ORE;
import static net.minecraft.block.Blocks.GRANITE;
import static net.minecraft.block.Blocks.GRASS_BLOCK;
import static net.minecraft.block.Blocks.GRAVEL;
import static net.minecraft.block.Blocks.IRON_BLOCK;
import static net.minecraft.block.Blocks.IRON_ORE;
import static net.minecraft.block.Blocks.JUNGLE_LOG;
import static net.minecraft.block.Blocks.JUNGLE_PLANKS;
import static net.minecraft.block.Blocks.JUNGLE_WOOD;
import static net.minecraft.block.Blocks.LAPIS_BLOCK;
import static net.minecraft.block.Blocks.LAPIS_ORE;
import static net.minecraft.block.Blocks.OAK_LOG;
import static net.minecraft.block.Blocks.OAK_PLANKS;
import static net.minecraft.block.Blocks.OAK_WOOD;
import static net.minecraft.block.Blocks.PODZOL;
import static net.minecraft.block.Blocks.POLISHED_ANDESITE;
import static net.minecraft.block.Blocks.POLISHED_BLACKSTONE;
import static net.minecraft.block.Blocks.POLISHED_DIORITE;
import static net.minecraft.block.Blocks.POLISHED_GRANITE;
import static net.minecraft.block.Blocks.REDSTONE_BLOCK;
import static net.minecraft.block.Blocks.REDSTONE_ORE;
import static net.minecraft.block.Blocks.SAND;
import static net.minecraft.block.Blocks.SANDSTONE;
import static net.minecraft.block.Blocks.SPRUCE_LOG;
import static net.minecraft.block.Blocks.SPRUCE_PLANKS;
import static net.minecraft.block.Blocks.SPRUCE_WOOD;
import static net.minecraft.block.Blocks.STONE;
import static net.minecraft.block.Blocks.STONE_BRICKS;
import static org.dimdev.dimdoors.block.ModBlocks.UNRAVELLED_FABRIC;

/**
 * Provides methods for applying Limbo decay. Limbo decay refers to the effect that most blocks placed in Limbo
 * naturally change into stone, then cobble, then gravel, and finally Unraveled Fabric as time passes.
 */
public final class LimboDecay {
	private static final Logger LOGGER = LogManager.getLogger();

	private static final Random RANDOM = new Random();

	/**
	 * Checks the blocks orthogonally around a given location (presumably the location of an Unraveled Fabric block)
	 * and applies Limbo decay to them. This gives the impression that decay spreads outward from Unraveled Fabric.
	 */
	public static void applySpreadDecay(World world, BlockPos pos) {
		//Check if we randomly apply decay spread or not. This can be used to moderate the frequency of
		//full spread decay checks, which can also shift its performance impact on the game.
		if (RANDOM.nextDouble() < DimensionalDoorsInitializer.getConfig().getLimboConfig().decaySpreadChance) {
			//Apply decay to the blocks above, below, and on all four sides.
			//World.getBlockId() implements bounds checking, so we don't have to worry about reaching out of the world
			boolean flag = decayBlock(world, pos.up());
			flag = flag && decayBlock(world, pos.down());
			flag = flag && decayBlock(world, pos.north());
			flag = flag && decayBlock(world, pos.south());
			flag = flag && decayBlock(world, pos.west());
			flag = flag && decayBlock(world, pos.east());
			if (flag) {
				LOGGER.debug("Applied limbo decay to block at all six sides at position {} in dimension {}", pos, world.getRegistryKey().getValue());
			}
		}
	}

	/**
	 * Checks if a block can be decayed and, if so, changes it to the next block ID along the decay sequence.
	 */
	private static boolean decayBlock(World world, BlockPos pos) {
		@NotNull Collection<DecayPattern> patterns = DecayLoader.getInstance().getPatterns();

		if(patterns.isEmpty()) return false;

		for (DecayPattern pattern : DecayLoader.getInstance().getPatterns()) {
			if(pattern.run(world, pos)) return true;
		}

		return false;
	}
}
