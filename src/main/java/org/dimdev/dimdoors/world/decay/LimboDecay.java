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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.dimdev.dimdoors.Constants;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.network.ExtendedServerPlayNetworkHandler;
import org.dimdev.dimdoors.network.packet.s2c.RenderBreakBlockS2CPacket;
import org.dimdev.dimdoors.sound.ModSoundEvents;

/**
 * Provides methods for applying Limbo decay. Limbo decay refers to the effect that most blocks placed in Limbo
 * naturally change into stone, then cobble, then gravel, and finally Unraveled Fabric as time passes.
 */
public final class LimboDecay {
	private static final Map<ResourceKey<Level>, Set<DecayTask>> DECAY_QUEUE = new HashMap<>();

	private static final RandomSource RANDOM = RandomSource.create();

	/**
	 * Checks the blocks orthogonally around a given location (presumably the location of an Unraveled Fabric block)
	 * and applies Limbo decay to them. This gives the impression that decay spreads outward from Unraveled Fabric.
	 */
	public static void applySpreadDecay(ServerLevel world, BlockPos pos) {
		//Check if we randomly apply decay spread or not. This can be used to moderate the frequency of
		//full spread decay checks, which can also shift its performance impact on the game.
		if (RANDOM.nextDouble() < Constants.CONFIG_MANAGER.get().getLimboConfig().decaySpreadChance) {
			BlockState origin = world.getBlockState(pos);

			//Apply decay to the blocks above, below, and on all four sides.
			// TODO: make max amount configurable
			int decayAmount = RANDOM.nextInt(5) + 1;
			List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));
			for (int i = 0; i < decayAmount; i++) {
				 decayBlock(world, pos.relative(directions.remove(RANDOM.nextInt(5 - i))), origin);
			}
		}
	}

	/**
	 * Checks if a block can be decayed and, if so, changes it to the next block ID along the decay sequence.
	 */
	public static void decayBlock(ServerLevel world, BlockPos pos, BlockState origin) {
		BlockState target = world.getBlockState(pos);

		Collection<DecayPattern> patterns = DecayLoader.getInstance().getPatterns(target.getBlock());

		if(patterns == null || patterns.isEmpty()) {
			return;
		}


		for(DecayPattern pattern : patterns) {
			if (!pattern.test(world, pos, origin, target)) {
				continue;
			}
			world.getPlayers(EntitySelector.withinDistance(pos.getX(), pos.getY(), pos.getZ(), 100)).forEach(player -> ExtendedServerPlayNetworkHandler.get(player.connection).getDimDoorsPacketHandler().sendPacket(new RenderBreakBlockS2CPacket(pos, 5)));
			world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSoundEvents.TEARING, SoundSource.BLOCKS, 0.5f, 1f);
			queueDecay(world, pos, origin, pattern, Constants.CONFIG_MANAGER.get().getLimboConfig().limboDecay);
			break;
		}
	}

	public static void queueDecay(ServerLevel world, BlockPos pos, BlockState origin, DecayPattern pattern, int delay) {
		DecayTask task = new DecayTask(pos, origin, pattern, delay);
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

	public static class DecayLoader extends SimplePreparableReloadListener<Void> {
		private static final DecayLoader INSTANCE = new DecayLoader();
		private final Map<Block, List<DecayPattern>> patterns = new HashMap<>();

		private DecayLoader() {
		}

		public static DecayLoader getInstance() {
			return INSTANCE;
		}

		@Override
		protected Void prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
			return null;
		}

		@Override
		protected void apply(Void s, ResourceManager manager, ProfilerFiller filler) {
			patterns.clear();
			CompletableFuture<List<DecayPattern>> futurePatternList = ResourceUtil.loadResourcePathToCollection(manager, "decay_patterns", ".json", new ArrayList<>(), ResourceUtil.NBT_READER.andThenReader(this::loadPattern));
			for (DecayPattern pattern : futurePatternList.join()) {
				for (Block block : pattern.constructApplicableBlocks()) {
					patterns.computeIfAbsent(block, (b) -> new ArrayList<>());
					patterns.get(block).add(pattern);
				}
			}
		}

		private DecayPattern loadPattern(Tag nbt, ResourceLocation ignored) {
			return DecayPattern.deserialize((CompoundTag) nbt);
		}

		public Collection<DecayPattern> getPatterns(Block block) {
			return patterns.get(block);
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

		public void process(ServerLevel world) {
			BlockState target = world.getBlockState(pos);
			if (world.hasChunkAt(pos) && processor.test(world, pos, origin, target)) {
				world.getPlayers(EntitySelector.withinDistance(pos.getX(), pos.getY(), pos.getZ(), 100))
						.forEach(player -> ExtendedServerPlayNetworkHandler.get(player.connection).getDimDoorsPacketHandler()
								.sendPacket(new RenderBreakBlockS2CPacket(pos, -1)));
				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), target.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.5f, 1f);
				processor.process(world, pos, origin, target);
			}
		}
	}
}
