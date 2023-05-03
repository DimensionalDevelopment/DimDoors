package org.dimdev.dimdoors.pockets.modifier;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;

public abstract class AbstractLazyModifier implements LazyModifier {
	private String resourceKey = null;

	@Override
	public void setResourceKey(String resourceKey) {
		this.resourceKey = resourceKey;
	}

	@Override
	public String getResourceKey() {
		return resourceKey;
	}

	@Override
	public NbtElement toNbt(NbtCompound nbt, boolean allowReference) {
		if (allowReference && this.getResourceKey() != null) {
			return NbtString.of(this.getResourceKey());
		}
		return toNbtInternal(nbt, allowReference);
	}

	// utility so the first part of toNbt can be extracted into default method
	// at this point we know for a fact, that we need to serialize into the NbtCompound
	// overwrite in subclass
	protected NbtCompound toNbtInternal(NbtCompound nbt, boolean allowReference) {
		return this.getType().toNbt(nbt);
	}
}
