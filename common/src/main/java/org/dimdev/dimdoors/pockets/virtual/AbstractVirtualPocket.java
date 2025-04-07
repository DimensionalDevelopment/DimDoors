package org.dimdev.dimdoors.pockets.virtual;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public abstract class AbstractVirtualPocket implements ImplementedVirtualPocket {
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
	public Tag toNbt(CompoundTag nbt, boolean allowReference) {
		if (allowReference && this.getResourceKey() != null) {
			return StringTag.valueOf(this.getResourceKey());
		}
		return toNbtInternal(nbt, allowReference);
	}

	// utility so the first part of toNbt can be extracted into default method
	// at this point we know for a fact, that we need to serialize into the CompoundTag
	// overwrite in subclass
	protected CompoundTag toNbtInternal(CompoundTag nbt, boolean allowReference) {
		return this.getType().toNbt(nbt);
	}
}
