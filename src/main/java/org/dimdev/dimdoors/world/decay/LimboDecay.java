package org.dimdev.dimdoors.world.decay;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.resource.ResourceManager;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.network.ExtendedServerPlayNetworkHandler;
import org.dimdev.dimdoors.network.packet.s2c.RenderBreakBlockS2CPacket;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.api.util.ResourceUtil;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Provides methods for applying Limbo decay. Limbo decay refers to the effect that most blocks placed in Limbo
 * naturally change into stone, then cobble, then gravel, and finally Unraveled Fabric as time passes.
 */
public final class LimboDecay {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<RegistryKey<World>, Set<DecayTask>> DECAY_QUEUE = new HashMap<>();
	// TODO: config
	private static final int DECAY_DELAY = 40;

	private static final Random RANDOM = new Random();

	/**
	 * Checks the blocks orthogonally around a given location (presumably the location of an Unraveled Fabric block)
	 * and applies Limbo decay to them. This gives the impression that decay spreads outward from Unraveled Fabric.
	 */
	public static void applySpreadDecay(ServerWorld world, BlockPos pos) {
		//Check if we randomly apply decay spread or not. This can be used to moderate the frequency of
		//full spread decay checks, which can also shift its performance impact on the game.
		if (RANDOM.nextDouble() < DimensionalDoorsInitializer.getConfig().getLimboConfig().decaySpreadChance) {
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
	private static void decayBlock(ServerWorld world, BlockPos pos, BlockState origin) {
		BlockState target = world.getBlockState(pos);

		Collection<DecayPattern> patterns = DecayLoader.getInstance().getPatterns(target.getBlock());

		if(patterns == null || patterns.isEmpty()) {
			return;
		}


		for(DecayPattern pattern : patterns) {
			if (!pattern.test(world, pos, origin, target)) {
				continue;
			}
			world.getPlayers(EntityPredicates.maxDistance(pos.getX(), pos.getY(), pos.getZ(), 100)).forEach(player -> {
				ExtendedServerPlayNetworkHandler.get(player.networkHandler).getDimDoorsPacketHandler().sendPacket(new RenderBreakBlockS2CPacket(pos, 5));
			});
			world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSoundEvents.TEARING, SoundCategory.BLOCKS, 0.5f, 1f);
			queueDecay(world, pos, origin, pattern, DECAY_DELAY);
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
		private final Map<Block, List<DecayPattern>> patterns = new HashMap();

		private DecayLoader() {
		}

		public static DecayLoader getInstance() {
			return INSTANCE;
		}

		@Override
		public void reload(ResourceManager manager) {
			patterns.clear();
			CompletableFuture<List<DecayPattern>> futurePatternList = ResourceUtil.loadResourcePathToCollection(manager, "decay_patterns", ".json", new ArrayList<>(), ResourceUtil.NBT_READER.andThenReader(this::loadPattern));
			for (DecayPattern pattern : futurePatternList.join()) {
				for (Block block : pattern.constructApplicableBlocks()) {
					patterns.computeIfAbsent(block, (b) -> new ArrayList<>());
					patterns.get(block).add(pattern);
				}
			}
		}

		private DecayPattern loadPattern(NbtElement nbt, Identifier ignored) {
			return DecayPattern.deserialize((NbtCompound) nbt);
		}

		public Collection<DecayPattern> getPatterns(Block block) {
			return patterns.get(block);
		}

		@Override
		public Identifier getFabricId() {
			return new Identifier("dimdoors", "decay_pattern");
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
			BlockState target = world.getBlockState(pos);
			if (world.isChunkLoaded(pos) && processor.test(world, pos, origin, target)) {
				world.getPlayers(EntityPredicates.maxDistance(pos.getX(), pos.getY(), pos.getZ(), 100)).forEach(player -> {
					ExtendedServerPlayNetworkHandler.get(player.networkHandler).getDimDoorsPacketHandler().sendPacket(new RenderBreakBlockS2CPacket(pos, -1));
				});
				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), target.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS, 0.5f, 1f);
				processor.process(world, pos, origin, world.getBlockState(pos));
			}
		}
	}
}
