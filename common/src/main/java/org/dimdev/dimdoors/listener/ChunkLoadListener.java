package org.dimdev.dimdoors.listener;

import dev.architectury.event.events.common.ChunkEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.dimdev.dimdoors.pockets.generator.LazyPocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.LazyCompatibleModifier;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.jetbrains.annotations.Nullable;

public class ChunkLoadListener implements ChunkEvent.LoadData {
	@Override
	public void load(ChunkAccess chunk, @Nullable ServerLevel world, CompoundTag nbt) {
		if(!(world != null && chunk instanceof LevelChunk levelChunk)) return;
		if (!ModDimensions.isPocketDimension(world)) return;
		Pocket pocket = DimensionalRegistry.getPocketDirectory(world.dimension()).getPocketAt(chunk.getPos().getWorldPosition());
		if (!(pocket instanceof LazyGenerationPocket)) return;
		if (LazyPocketGenerator.currentlyGenerating) {
			LazyPocketGenerator.generationQueue.add(levelChunk);
		} else {
			LazyCompatibleModifier.runQueuedModifications(levelChunk);
			((LazyGenerationPocket) pocket).chunkLoaded(levelChunk);
		}
	}
}
