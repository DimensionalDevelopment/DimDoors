package org.dimdev.dimdoors.world.level.component;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import org.dimdev.dimdoors.api.capability.IComponent;

public class ChunkLazilyGeneratedComponent implements IComponent {
	private boolean hasBeenLazyGenned = false;

	public boolean hasBeenLazyGenned() {
		return hasBeenLazyGenned;
	}

	public void setGenned() {
		if (hasBeenLazyGenned) throw new UnsupportedOperationException("This chunk seems to have already been lazily generated!");
		hasBeenLazyGenned = true;
	}

	@Override
	public void readFromNbt(CompoundTag nbt) {
		if (nbt.contains("has_been_lazy_genned", Tag.TAG_INT))
			hasBeenLazyGenned = nbt.getInt("has_been_lazy_genned") == 1;
	}

	@Override
	public CompoundTag writeToNbt(CompoundTag nbt) {
		if (hasBeenLazyGenned)
			nbt.putInt("has_been_lazy_genned", 1);
		return nbt;
	}
}
