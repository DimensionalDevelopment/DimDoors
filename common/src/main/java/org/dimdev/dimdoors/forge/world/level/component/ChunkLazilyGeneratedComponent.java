package org.dimdev.dimdoors.forge.world.level.component;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.chunk.LevelChunk;

public class ChunkLazilyGeneratedComponent {
	private boolean hasBeenLazyGenned = false;

	public boolean hasBeenLazyGenned() {
		return hasBeenLazyGenned;
	}

	public void setGenned() {
		if (hasBeenLazyGenned) throw new UnsupportedOperationException("This chunk seems to have already been lazily generated!");
		hasBeenLazyGenned = true;
	}

	@ExpectPlatform
	public static ChunkLazilyGeneratedComponent get(LevelChunk chunk) {
		throw new RuntimeException();
	}

	public void readFromNbt(CompoundTag nbt) {
		if (nbt.contains("has_been_lazy_genned", Tag.TAG_INT)) {
			hasBeenLazyGenned = nbt.getInt("has_been_lazy_genned") == 1;
		}
	}

	public void writeToNbt(CompoundTag nbt) {
		if (hasBeenLazyGenned) {
			nbt.putInt("has_been_lazy_genned", 1);
		}
	}
}
