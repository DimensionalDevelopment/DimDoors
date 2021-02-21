package org.dimdev.dimdoors.world.level;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.chunk.Chunk;

public class ChunkLazilyGenerated implements Component {
	private boolean hasBeenLazyGenned = false;

	public boolean hasBeenLazyGenned() {
		return hasBeenLazyGenned;
	}

	public void setGenned() {
		if (hasBeenLazyGenned) throw new UnsupportedOperationException("This chunk seems to have already been lazily generated!");
		hasBeenLazyGenned = true;
	}

	public static ChunkLazilyGenerated get(Chunk chunk) {
		return DimensionalDoorsComponents.CHUNK_LAZILY_GENERATED_COMPONENT_KEY.get(chunk);
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		if (tag.contains("has_been_lazy_genned", NbtType.INT)) {
			hasBeenLazyGenned = tag.getInt("has_been_lazy_genned") == 1;
		}
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		if (hasBeenLazyGenned) {
			tag.putInt("has_been_lazy_genned", 1);
		}
	}
}
