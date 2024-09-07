package org.dimdev.dimdoors.world.decay;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.network.ExtendedServerPlayNetworkHandler;
import org.dimdev.dimdoors.network.packet.s2c.RenderBreakBlockS2CPacket;
import org.dimdev.dimdoors.sound.ModSoundEvents;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Provides methods for applying decay. Decay refers to the effect that most blocks placed next to certain blocks like unraveled fabric
 * change into simpler forms ultimately becoming in most cases unraveled Fabric as time passes.
 */
public final class Decay {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<ResourceKey<Level>, Set<DecayTask>> DECAY_QUEUE = new HashMap<>();

	/**
	 * Checks the blocks orthogonally around a given location (presumably the location of an Unraveled Fabric block)
	 * and applies Limbo decay to them. This gives the impression that decay spreads outward from Unraveled Fabric.
	 */
	public static void applySpreadDecay(ServerLevel world, BlockPos pos, RandomSource random, DecaySource source) {
		//Check if we randomly apply decay spread or not. This can be used to moderate the frequency of
		//full spread decay checks, which can also shift its performance impact on the game.
		if (random.nextDouble() < DimensionalDoors.getConfig().getDecayConfig().decaySpreadChance) {
			BlockState origin = world.getBlockState(pos);

			//Apply decay to the blocks above, below, and on all four sides.
			// TODO: make max amount configurable
			int decayAmount = random.nextInt(5) + 1;
			List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));
			for (int i = 0; i < decayAmount; i++) {
				 decayBlock(world, pos.relative(directions.remove(random.nextInt(5 - i))), origin, source);
			}
		}
	}

	/**
	 * Checks if a block can be decayed and, if so, changes it to the next block ID along the decay sequence.
	 */
	public static void decayBlock(ServerLevel world, BlockPos pos, BlockState origin, DecaySource source) {
		BlockState targetState = world.getBlockState(pos);
		FluidState fluidState = world.getFluidState(pos);

		Collection<DecayPattern> patterns = DecayLoader.getInstance().getPatterns(targetState.getBlockHolder().unwrapKey().get());

		if(patterns.isEmpty()) patterns = DecayLoader.getInstance().getPatterns(fluidState.getType().builtInRegistryHolder().key());

		if(patterns.isEmpty()) {
			return;
		}

		for(DecayPattern pattern : patterns) {
			if (!world.isNaturalSpawningAllowed(pos) || !pattern.test(world, pos, origin, targetState, fluidState, source)) {
				continue;
			}
			world.getPlayers(EntitySelector.withinDistance(pos.getX(), pos.getY(), pos.getZ(), 100)).forEach(player -> {
				ExtendedServerPlayNetworkHandler.get(player.connection).getDimDoorsPacketHandler().sendPacket(new RenderBreakBlockS2CPacket(pos, 5));
			});
			world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSoundEvents.TEARING.get(), SoundSource.BLOCKS, 0.5f, 1f);
			queueDecay(world, pos, origin, pattern, source, DimensionalDoors.getConfig().getDecayConfig().decayDelay);
			break;
		}
	}

	public static void queueDecay(ServerLevel world, BlockPos pos, BlockState origin, DecayPattern pattern, DecaySource source, int delay) {
		DecayTask task = new DecayTask(pos, origin, pattern, source, delay);
		if (delay <= 0) {
			task.process(world);
		} else {
			DECAY_QUEUE.computeIfAbsent(world.dimension(), k -> new HashSet<>()).add(task);
		}
	}

	public static void tick(ServerLevel world) {
		ResourceKey<Level> key = world.dimension();
		if (DECAY_QUEUE.containsKey(key)) {
			Set<DecayTask> tasks = DECAY_QUEUE.get(key);
			Set<DecayTask> tasksToRun = tasks.stream().filter(DecayTask::reduceDelayIsDone).collect(Collectors.toSet());
			tasks.removeAll(tasksToRun);
			tasksToRun.forEach(task -> task.process(world));
		}
	}

    public static class DecayLoader implements ResourceManagerReloadListener {
		private static final Logger LOGGER = LogManager.getLogger();
		private static final DecayLoader INSTANCE = new DecayLoader();
		private final Map<ResourceKey<Block>, List<DecayPattern>> blockPatterns = new HashMap<>();
		private final Map<ResourceKey<Fluid>, List<DecayPattern>> fluidPatterns = new HashMap<>();

		private DecayLoader() {
		}

		public static DecayLoader getInstance() {
			return INSTANCE;
		}

		@Override
		public void onResourceManagerReload(ResourceManager manager) {
			blockPatterns.clear();
			CompletableFuture<List<DecayPattern>> futurePatternList = ResourceUtil.loadResourcePathToCollection(manager, "decay_patterns", ".json", new ArrayList<>(), ResourceUtil.JSON_READER.andThenReader(this::loadPattern));
			for (DecayPattern pattern : futurePatternList.join()) {
				for (ResourceKey<Block> block : pattern.constructApplicableBlocks()) {
					blockPatterns.computeIfAbsent(block, (b) -> new ArrayList<>());
					blockPatterns.get(block).add(pattern);
				}

				for (ResourceKey<Fluid> fluid : pattern.constructApplicableFluids()) {
					fluidPatterns.computeIfAbsent(fluid, (b) -> new ArrayList<>());
					fluidPatterns.get(fluid).add(pattern);
				}
			}
		}

		private DecayPattern loadPattern(JsonElement json, ResourceLocation ignored) {
			return JsonOps.INSTANCE.withDecoder(DecayPattern.CODEC).apply(json).getOrThrow(false, Decay.LOGGER::error).getFirst();
		}

		public Collection<DecayPattern> getPatterns(Object object) {
			if(object instanceof ResourceKey<?> key) {
				if (key.isFor(Registry.BLOCK.key()) && blockPatterns.containsKey(key)) return blockPatterns.get(key);
				else if (key.isFor(Registry.FLUID.key()) && fluidPatterns.containsKey(key)) return fluidPatterns.get(key);
			}

			return Collections.emptyList();
		}

		public Collection<DecayPattern> getPatterns(ResourceKey<Fluid> fluid) {
			return fluidPatterns.getOrDefault(fluid, Collections.emptyList());
		}

        public Map<ResourceKey<Block>, List<DecayPattern>> getBlockPatterns() {
			return blockPatterns;
        }
    }

	private static class DecayTask {
		private final BlockPos pos;
		private final BlockState origin;
		private final DecayPattern processor;
		private final DecaySource source;
		private int delay;


		public DecayTask(BlockPos pos, BlockState origin, DecayPattern processor, DecaySource source, int delay) {
			this.pos = pos;
			this.origin = origin;
			this.processor = processor;
			this.source = source;
			this.delay = delay;
		}

		public boolean reduceDelayIsDone() {
			return --delay <= 0;
		}

		public void process(ServerLevel world) {
			BlockState targetBlock = world.getBlockState(pos);
			FluidState targetFluid = world.getFluidState(pos);

			world.getPlayers(EntitySelector.withinDistance(pos.getX(), pos.getY(), pos.getZ(), 100)).forEach(player -> {
				ExtendedServerPlayNetworkHandler.get(player.connection).getDimDoorsPacketHandler().sendPacket(new RenderBreakBlockS2CPacket(pos, -1));
			});

			world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), targetBlock.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.5f, 1f);

			if (source.decayIntoWorldThread()) {
				if (DimensionalDoors.getConfig().getDecayConfig().decaysIntoAir)
					world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				else processor.process(world, pos, origin, targetBlock, targetFluid, source);
			} else {
				processor.process(world, pos, origin, targetBlock, targetFluid, source);
			}
		}
	}
}
