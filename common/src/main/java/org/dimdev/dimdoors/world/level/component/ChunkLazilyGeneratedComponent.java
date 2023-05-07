package org.dimdev.dimdoors.world.level.component;

import dev.onyxstudios.cca.api.v3.component.Component;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.Tag;
import net.minecraft.world.chunk.Chunk;

import net.fabricmc.fabric.api.util.NbtType;

import net.minecraft.world.level.chunk.ChunkAccess;
import org.dimdev.dimdoors.DimensionalDoorsComponents;

public class ChunkLazilyGeneratedComponent implements Component {
	private boolean hasBeenLazyGenned = false;

	public boolean hasBeenLazyGenned() {
		return hasBeenLazyGenned;
	}

	public void setGenned() {
		if (hasBeenLazyGenned) throw new UnsupportedOperationException("This chunk seems to have already been lazily generated!");
		hasBeenLazyGenned = true;
	}

	public static ChunkLazilyGeneratedComponent get(ChunkAccess chunk) {
		return DimensionalDoorsComponents.CHUNK_LAZILY_GENERATED_COMPONENT_KEY.get(chunk); //TODO: Multiplatform
	}

	@Override
	public void readFromNbt(CompoundTag nbt) {
		if (nbt.contains("has_been_lazy_genned", Tag.TAG_INT)) {
			hasBeenLazyGenned = nbt.getInt("has_been_lazy_genned") == 1;
		}
	}

	@Override
	public void writeToNbt(CompoundTag nbt) {
		if (hasBeenLazyGenned) {
			nbt.putInt("has_been_lazy_genned", 1);
		}
	}
}
