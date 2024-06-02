package org.dimdev.dimdoors.listener;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.dimdev.dimdoors.api.event.ChunkServedCallback;
import org.dimdev.dimdoors.pockets.generator.LazyPocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.LazyCompatibleModifier;
import org.dimdev.dimdoors.forge.world.ModDimensions;
import org.dimdev.dimdoors.forge.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.forge.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.forge.world.pocket.type.Pocket;

public class ChunkLoadListener implements ChunkServedCallback {
	@Override
	public void onChunkServed(ServerLevel level, LevelChunk chunk) {
		if(level == null) return;
		if (!ModDimensions.isPocketDimension(level)) return;
		Pocket pocket = DimensionalRegistry.getPocketDirectory(level.dimension()).getPocketAt(chunk.getPos().getWorldPosition());
		if (!(pocket instanceof LazyGenerationPocket)) return;
		if (LazyPocketGenerator.currentlyGenerating) {
			LazyPocketGenerator.generationQueue.add(chunk);
		} else {
			LazyCompatibleModifier.runQueuedModifications(chunk);
			((LazyGenerationPocket) pocket).chunkLoaded(chunk);
		}
	}
}
