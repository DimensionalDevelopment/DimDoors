package org.dimdev.dimdoors.pockets.modifier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;

public interface LazyModifier extends Modifier{
	default CompoundTag toTag(CompoundTag tag) {
		return Modifier.super.toTag(tag);
	}

	void applyToChunk(LazyGenerationPocket pocket, Chunk chunk);
}
