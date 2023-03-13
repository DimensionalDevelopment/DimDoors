package org.dimdev.dimdoors.listener;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.dimdev.dimdoors.pockets.generator.LazyPocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.LazyCompatibleModifier;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class ChunkLoadListener implements ServerChunkEvents.Load {
	@Override
	public void onChunkLoad(ServerLevel world, LevelChunk chunk) {
		if (!ModDimensions.isPocketDimension(world)) return;
		Pocket pocket = DimensionalRegistry.getPocketDirectory(world.dimension()).getPocketAt(chunk.getPos().getWorldPosition());
		if (!(pocket instanceof LazyGenerationPocket)) return;
		if (LazyPocketGenerator.currentlyGenerating) {
			LazyPocketGenerator.generationQueue.add(chunk);
		} else {
			LazyCompatibleModifier.runQueuedModifications(chunk);
			((LazyGenerationPocket) pocket).chunkLoaded(chunk);
		}
	}
}
