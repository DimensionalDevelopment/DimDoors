package org.dimdev.dimdoors.pockets.modifier;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;

public interface LazyModifier extends Modifier {
	default NbtCompound toNbt(NbtCompound nbt) {
		return Modifier.super.toNbt(nbt);
	}

	void applyToChunk(LazyGenerationPocket pocket, Chunk chunk);
}
