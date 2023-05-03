package org.dimdev.dimdoors.world.decay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.network.ExtendedServerPlayNetworkHandler;
import org.dimdev.dimdoors.network.packet.s2c.RenderBreakBlockS2CPacket;
import org.dimdev.dimdoors.sound.ModSoundEvents;

/**
 * Provides methods for applying Limbo decay. Limbo decay refers to the effect that most blocks placed in Limbo
 * naturally change into stone, then cobble, then gravel, and finally Unraveled Fabric as time passes.
 */
public final class LimboDecay {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<RegistryKey<World>, Set<DecayTask>> DECAY_QUEUE = new HashMap<>();

	private static final Random RANDOM = Random.create();

	/**
	 * Checks the blocks orthogonally around a given location (presumably the location of an Unraveled Fabric block)
	 * and applies Limbo decay to them. This gives the impression that decay spreads outward from Unraveled Fabric.
	 */
	public static void applySpreadDecay(ServerWorld world, BlockPos pos) {
		//Check if we randomly apply decay spread or not. This can be used to moderate the frequency of
		//full spread decay checks, which can also shift its performance impact on the game.
		if (RANDOM.nextDouble() < DimensionalDoors.getConfig().getLimboConfig().decaySpreadChance) {
			BlockState origin = world.getBlockState(pos);

			//Apply decay to the blocks above, below, and on all four sides.
			// TODO: make max amount configurable
			int decayAmount = RANDOM.nextInt(5) + 1;
			List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));
			for (int i = 0; i < decayAmount; i++) {
				 decayBlock(world, pos.offset(directions.remove(RANDOM.nextInt(5 - i))), origin);
			}
		}
	}

	/**
	 * Checks if a block can be decayed and, if so, changes it to the next block ID along the decay sequence.
	 */
	public static void decayBlock(ServerWorld world, BlockPos pos, BlockState origin) {
		BlockState targetState = world.getBlockState(pos);
		FluidState fluidState = world.getFluidState(pos);

		Collection<DecayPattern> patterns = DecayLoader.getInstance().getPatterns(targetState.getBlock());

		if(patterns.isEmpty()) patterns = DecayLoader.getInstance().getPatterns(fluidState.getFluid());

		if(patterns.isEmpty()) {
			return;
		}


		for(DecayPattern pattern : patterns) {
			if (!pattern.test(world, pos, origin, targetState, fluidState)) {
				continue;
			}
			world.getPlayers(EntityPredicates.maxDistance(pos.getX(), pos.getY(), pos.getZ(), 100)).forEach(player -> {
				ExtendedServerPlayNetworkHandler.get(player.networkHandler).getDimDoorsPacketHandler().sendPacket(new RenderBreakBlockS2CPacket(pos, 5));
			});
			world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSoundEvents.TEARING, SoundCategory.BLOCKS, 0.5f, 1f);
			queueDecay(world, pos, origin, pattern, DimensionalDoors.getConfig().getLimboConfig().limboDecay);
			break;
		}
	}

	public static void queueDecay(ServerWorld world, BlockPos pos, BlockState origin, DecayPattern pattern, int delay) {
		DecayTask task = new DecayTask(pos, origin, pattern, delay);
		if (delay <= 0) {
			task.process(world);
		} else {
			DECAY_QUEUE.computeIfAbsent(world.getRegistryKey(), k -> new HashSet<>()).add(task);
		}
	}

	public static void tick(ServerWorld world) {
		RegistryKey<World> key = world.getRegistryKey();
		if (DECAY_QUEUE.containsKey(key)) {
			Set<DecayTask> tasks = DECAY_QUEUE.get(key);
			Set<DecayTask> tasksToRun = tasks.stream().filter(DecayTask::reduceDelayIsDone).collect(Collectors.toSet());
			tasks.removeAll(tasksToRun);
			tasksToRun.forEach(task -> task.process(world));
		}
	}

	public static class DecayLoader implements SimpleSynchronousResourceReloadListener {
		private static final Logger LOGGER = LogManager.getLogger();
		private static final DecayLoader INSTANCE = new DecayLoader();
		private final Map<Block, List<DecayPattern>> blockPatterns = new HashMap<>();
		private final Map<Fluid, List<DecayPattern>> fluidPatterns = new HashMap<>();

		private DecayLoader() {
		}

		public static DecayLoader getInstance() {
			return INSTANCE;
		}

		@Override
		public void reload(ResourceManager manager) {
			blockPatterns.clear();
			CompletableFuture<List<DecayPattern>> futurePatternList = ResourceUtil.loadResourcePathToCollection(manager, "decay_patterns", ".json", new ArrayList<>(), ResourceUtil.NBT_READER.andThenReader(this::loadPattern));
			for (DecayPattern pattern : futurePatternList.join()) {
				for (Block block : pattern.constructApplicableBlocks()) {
					blockPatterns.computeIfAbsent(block, (b) -> new ArrayList<>());
					blockPatterns.get(block).add(pattern);
				}

				for (Fluid fluid : pattern.constructApplicableFluids()) {
					fluidPatterns.computeIfAbsent(fluid, (b) -> new ArrayList<>());
					fluidPatterns.get(fluid).add(pattern);
				}
			}
		}

		private DecayPattern loadPattern(NbtElement nbt, Identifier ignored) {
			return DecayPattern.deserialize((NbtCompound) nbt);
		}

		public Collection<DecayPattern> getPatterns(Block block) {
			return blockPatterns.getOrDefault(block, new ArrayList<>());
		}

		public Collection<DecayPattern> getPatterns(Fluid fluid) {
			return fluidPatterns.getOrDefault(fluid, new ArrayList<>());
		}

		@Override
		public Identifier getFabricId() {
			return DimensionalDoors.id("decay_pattern");
		}
	}

	private static class DecayTask {
		private final BlockPos pos;
		private final BlockState origin;
		private final DecayPattern processor;
		private int delay;


		public DecayTask(BlockPos pos, BlockState origin, DecayPattern processor, int delay) {
			this.pos = pos;
			this.origin = origin;
			this.processor = processor;
			this.delay = delay;
		}

		public boolean reduceDelayIsDone() {
			return --delay <= 0;
		}

		public void process(ServerWorld world) {
			BlockState targetBlock = world.getBlockState(pos);
			FluidState targetFluid = world.getFluidState(pos);
			if (world.isChunkLoaded(pos) && processor.test(world, pos, origin, targetBlock, targetFluid)) {
				world.getPlayers(EntityPredicates.maxDistance(pos.getX(), pos.getY(), pos.getZ(), 100)).forEach(player -> {
					ExtendedServerPlayNetworkHandler.get(player.networkHandler).getDimDoorsPacketHandler().sendPacket(new RenderBreakBlockS2CPacket(pos, -1));
				});
				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), targetBlock.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS, 0.5f, 1f);
				processor.process(world, pos, origin, targetBlock, targetFluid);
			}
		}
	}
}
