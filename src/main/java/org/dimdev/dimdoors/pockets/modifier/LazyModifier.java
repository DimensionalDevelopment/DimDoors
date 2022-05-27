package org.dimdev.dimdoors.pockets.modifier;

import net.minecraft.world.chunk.Chunk;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;

public interface LazyModifier extends Modifier {
	void applyToChunk(LazyGenerationPocket pocket, Chunk chunk);
}
