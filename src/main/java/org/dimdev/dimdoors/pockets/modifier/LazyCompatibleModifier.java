package org.dimdev.dimdoors.pockets.modifier;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.*;
import java.util.function.Consumer;

public interface LazyCompatibleModifier extends Modifier {
	LinkedHashMap<ChunkPos, Queue<Consumer<Chunk>>> chunkModificationQueue = new LinkedHashMap<>();

	static void runQueuedModifications(Chunk chunk) {
		Queue<Consumer<Chunk>> tasks = chunkModificationQueue.remove(chunk.getPos());
		if (tasks == null) return;
		Iterator<Consumer<Chunk>> iterator = tasks.iterator();
		while (iterator.hasNext()) {
			Consumer<Chunk> task = iterator.next();
			iterator.remove();
			task.accept(chunk);
		}
	}

	static void runLeftoverModifications(World world) {
		new HashSet<>(chunkModificationQueue.keySet()).forEach(chunkPos -> world.getChunk(chunkPos.getStartPos())); // seems safest for Cubic Chunks reasons;
	}

	default void queueChunkModificationTask(ChunkPos pos, Consumer<Chunk> task) {
		chunkModificationQueue.compute(pos, ((chunkPos, chunkTaskQueue) -> {
			if (chunkTaskQueue == null) chunkTaskQueue = new LinkedList<>();
			chunkTaskQueue.add(task);
			return chunkTaskQueue;
		}));
	}

	@Override
	default NbtCompound toNbt(NbtCompound nbt) {
		return Modifier.super.toNbt(nbt);
	}
}
