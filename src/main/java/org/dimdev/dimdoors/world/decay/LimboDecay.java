package org.dimdev.dimdoors.world.decay;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

import net.minecraft.block.BlockState;
import net.minecraft.resource.ResourceManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.api.util.Path;
import org.dimdev.dimdoors.api.util.SimpleTree;
import org.dimdev.dimdoors.world.decay.DecayPattern;
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
			//World.getBlockId() implements bounds checking, so we don't have to worry about reaching out of the world/*
			/*boolean flag = */decayBlock(world, pos.up(), origin);
			/*flag = flag && */decayBlock(world, pos.down(), origin);
			/*flag = flag && */decayBlock(world, pos.north(), origin);
			/*flag = flag && */decayBlock(world, pos.south(), origin);
			/*flag = flag && */decayBlock(world, pos.west(), origin);
			/*flag = flag && */decayBlock(world, pos.east(), origin);
//			if (flag) {
//				LOGGER.debug("Applied limbo decay to block at all six sides at position {} in dimension {}", pos, world.getRegistryKey().getValue());
//			}
		}
	}

	/**
	 * Checks if a block can be decayed and, if so, changes it to the next block ID along the decay sequence.
	 */
	private static boolean decayBlock(World world, BlockPos pos, BlockState origin) {
		@NotNull Collection<DecayPattern> patterns = DecayLoader.getInstance().getPatterns();

		if(patterns.isEmpty()) return false;

		BlockState target = world.getBlockState(pos);

		for (DecayPattern pattern : DecayLoader.getInstance().getPatterns()) {
			if(pattern.run(world, pos, origin, target)) return true;
		}

		return false;
	}

	public static class DecayLoader implements SimpleSynchronousResourceReloadListener {
		private static final Logger LOGGER = LogManager.getLogger();
		private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
		private static final DecayLoader INSTANCE = new DecayLoader();
		private SimpleTree<String, DecayPattern> patterns = new SimpleTree<>(String.class);

		private DecayLoader() {
		}

		public static DecayLoader getInstance() {
			return INSTANCE;
		}

		@Override
		public void reload(ResourceManager manager) {
			patterns.clear();
			CompletableFuture<SimpleTree<String, DecayPattern>> futurePatternMap = loadResourcePathFromJsonToTree(manager, "decay_patterns", this::loadPattern);
			patterns = futurePatternMap.join();
		}

		private DecayPattern loadPattern(JsonObject object) {
			return DecayPattern.deserialize(object);
		}

		private <T> CompletableFuture<SimpleTree<String, T>> loadResourcePathFromJsonToTree(ResourceManager manager, String startingPath, Function<JsonObject, T> reader) {
			int sub = startingPath.endsWith("/") ? 0 : 1;

			Collection<Identifier> ids = manager.findResources(startingPath, str -> str.endsWith(".json"));
			return CompletableFuture.supplyAsync(() -> {
				SimpleTree<String, T> tree = new SimpleTree<>(String.class);
				tree.putAll(ids.parallelStream().unordered().collect(Collectors.toConcurrentMap(
						id -> Path.stringPath(id.getNamespace() + ":" + id.getPath().substring(0, id.getPath().lastIndexOf(".")).substring(startingPath.length() + sub)),
						id -> {
							try {
								JsonElement json = GSON.fromJson(new InputStreamReader(manager.getResource(id).getInputStream()), JsonElement.class);
								return reader.apply(json.getAsJsonObject());
							} catch (IOException e) {
								throw new RuntimeException("Error loading resource: " + id);
							}
						})));
				return tree;
			});
		}

		public @NotNull Collection<DecayPattern> getPatterns() {
			return patterns.values();
		}

		@Override
		public Identifier getFabricId() {
			return new Identifier("dimdoors", "decay_pattern");
		}
	}
}
