package org.dimdev.dimdoors.pockets.modifier;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface LazyCompatibleModifier extends Modifier {
	LinkedHashMap<ChunkPos, Queue<Consumer<ChunkAccess>>> chunkModificationQueue = new LinkedHashMap<>();

	static void runQueuedModifications(ChunkAccess chunk) {
		Queue<Consumer<ChunkAccess>> tasks = chunkModificationQueue.remove(chunk.getPos());
		if (tasks == null) return;
		Iterator<Consumer<ChunkAccess>> iterator = tasks.iterator();
		while (iterator.hasNext()) {
			Consumer<ChunkAccess> task = iterator.next();
			iterator.remove();
			task.accept(chunk);
		}
	}

	static void runLeftoverModifications(Level world) {
		new HashSet<>(chunkModificationQueue.keySet()).forEach(chunkPos -> world.getChunk(chunkPos.getWorldPosition())); // seems safest for Cubic Chunks reasons;
	}

	default void queueChunkModificationTask(ChunkPos pos, Consumer<ChunkAccess> task) {
		chunkModificationQueue.compute(pos, ((chunkPos, chunkTaskQueue) -> {
			if (chunkTaskQueue == null) chunkTaskQueue = new LinkedList<>();
			chunkTaskQueue.add(task);
			return chunkTaskQueue;
		}));
	}
}
