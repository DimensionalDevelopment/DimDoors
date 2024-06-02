package org.dimdev.dimdoors.pockets.modifier;

import net.minecraft.world.level.chunk.ChunkAccess;
import org.dimdev.dimdoors.forge.world.pocket.type.LazyGenerationPocket;

public interface LazyModifier extends Modifier {
	void applyToChunk(LazyGenerationPocket pocket, ChunkAccess chunk);
}
