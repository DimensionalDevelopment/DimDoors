package org.dimdev.dimdoors.rift.registry;

import net.minecraft.nbt.CompoundTag;

public class RiftPlaceholder extends Rift { // TODO: don't extend rift
	@Override
	public void sourceGone(RegistryVertex source) {
	}

	@Override
	public void targetGone(RegistryVertex target) {
	}

	@Override
	public void sourceAdded(RegistryVertex source) {
	}

	@Override
	public void targetAdded(RegistryVertex target) {
	}

	@Override
	public void targetChanged(RegistryVertex target) {
	}

	@Override
	public void markDirty() {

	}

	@Override
	public RegistryVertexType<? extends RegistryVertex> getType() {
		return RegistryVertexType.RIFT_PLACEHOLDER.get();
	}

	public static CompoundTag toNbt(RiftPlaceholder vertex) {
		CompoundTag nbt = new CompoundTag();
		nbt.putUUID("id", vertex.id);
		return nbt;
	}

	public static RiftPlaceholder fromNbt(CompoundTag nbt) {
		RiftPlaceholder vertex = new RiftPlaceholder();
		vertex.id = nbt.getUUID("id");
		return vertex;
	}
}
