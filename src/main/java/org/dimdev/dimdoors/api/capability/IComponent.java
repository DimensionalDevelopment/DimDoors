package org.dimdev.dimdoors.api.capability;

import net.minecraft.nbt.CompoundTag;

public interface IComponent {
	CompoundTag writeToNbt(CompoundTag nbt);
	void readFromNbt(CompoundTag nbt);
}
