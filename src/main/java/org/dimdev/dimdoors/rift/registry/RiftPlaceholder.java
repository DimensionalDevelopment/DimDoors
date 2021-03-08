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
		return RegistryVertexType.RIFT_PLACEHOLDER;
	}

	public static CompoundTag toTag(RiftPlaceholder vertex) {
		CompoundTag tag = new CompoundTag();
		tag.putUuid("id", vertex.id);
		return tag;
	}

	public static RiftPlaceholder fromTag(CompoundTag tag) {
		RiftPlaceholder vertex = new RiftPlaceholder();
		vertex.id = tag.getUuid("id");
		return vertex;
	}
}
