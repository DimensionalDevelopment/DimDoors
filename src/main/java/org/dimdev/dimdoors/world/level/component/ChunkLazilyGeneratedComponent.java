package org.dimdev.dimdoors.world.level.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import org.dimdev.dimdoors.DimensionalDoorsComponents;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.chunk.Chunk;

public class ChunkLazilyGeneratedComponent implements Component {
	private boolean hasBeenLazyGenned = false;

	public boolean hasBeenLazyGenned() {
		return hasBeenLazyGenned;
	}

	public void setGenned() {
		if (hasBeenLazyGenned) throw new UnsupportedOperationException("This chunk seems to have already been lazily generated!");
		hasBeenLazyGenned = true;
	}

	public static ChunkLazilyGeneratedComponent get(Chunk chunk) {
		return DimensionalDoorsComponents.CHUNK_LAZILY_GENERATED_COMPONENT_KEY.get(chunk);
	}

	@Override
	public void readFromNbt(NbtCompound nbt) {
		if (nbt.contains("has_been_lazy_genned", NbtType.INT)) {
			hasBeenLazyGenned = nbt.getInt("has_been_lazy_genned") == 1;
		}
	}

	@Override
	public void writeToNbt(NbtCompound nbt) {
		if (hasBeenLazyGenned) {
			nbt.putInt("has_been_lazy_genned", 1);
		}
	}
}
