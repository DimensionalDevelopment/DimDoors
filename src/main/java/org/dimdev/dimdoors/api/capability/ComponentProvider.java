package org.dimdev.dimdoors.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class ComponentProvider<I extends IComponent> implements ICapabilitySerializable<CompoundTag> {

	public Capability<IComponent> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

	private final I type;

	public ComponentProvider(I type) {
		this.type = type;
	}

	public I getWrappedType() {
		return this.type;
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return cap == CAPABILITY ? LazyOptional.of(() -> type).cast() : LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		return type.writeToNbt(new CompoundTag());
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		type.readFromNbt(nbt);
	}
}
