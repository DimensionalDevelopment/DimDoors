package org.dimdev.dimdoors.world.decay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.resource.ResourceManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.util.ResourceUtil;
import org.jetbrains.annotations.NotNull;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
			BlockState origin = world.getBlockState(pos);

			//Apply decay to the blocks above, below, and on all four sides.
			decayBlock(world, pos.up(), origin);
			decayBlock(world, pos.down(), origin);
			decayBlock(world, pos.north(), origin);
			decayBlock(world, pos.south(), origin);
			decayBlock(world, pos.west(), origin);
			decayBlock(world, pos.east(), origin);
		}
	}

	/**
	 * Checks if a block can be decayed and, if so, changes it to the next block ID along the decay sequence.
	 */
	private static void decayBlock(World world, BlockPos pos, BlockState origin) {
		@NotNull Collection<DecayPattern> patterns = DecayLoader.getInstance().getPatterns();

		if(patterns.isEmpty()) return;

		BlockState target = world.getBlockState(pos);

		patterns.stream().filter(decayPattern -> decayPattern.test(world, pos, origin, target)).findAny().ifPresent(pattern -> pattern.process(world, pos, origin, target));
	}

	public static class DecayLoader implements SimpleSynchronousResourceReloadListener {
		private static final Logger LOGGER = LogManager.getLogger();
		private static final DecayLoader INSTANCE = new DecayLoader();
		private List<DecayPattern> patterns = new ArrayList<>();

		private DecayLoader() {
		}

		public static DecayLoader getInstance() {
			return INSTANCE;
		}

		@Override
		public void reload(ResourceManager manager) {
			patterns.clear();
			CompletableFuture<List<DecayPattern>> futurePatternMap = ResourceUtil.loadResourcePathToCollection(manager, "decay_patterns", ".json", new ArrayList<>(), ResourceUtil.NBT_READER.andThenReader(this::loadPattern));
			patterns = futurePatternMap.join();
		}

		private DecayPattern loadPattern(NbtElement nbt, Identifier ignored) {
			return DecayPattern.deserialize((NbtCompound) nbt);
		}

		public @NotNull Collection<DecayPattern> getPatterns() {
			return patterns;
		}

		@Override
		public Identifier getFabricId() {
			return new Identifier("dimdoors", "decay_pattern");
		}
	}
}
