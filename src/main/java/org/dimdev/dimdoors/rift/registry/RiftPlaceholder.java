package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.dynamic.DynamicSerializableUuid;

public class RiftPlaceholder extends Rift { // TODO: don't extend rift
	private static final Logger LOGGER = LogManager.getLogger();

	public static Codec<RiftPlaceholder> CODEC = DynamicSerializableUuid.CODEC.xmap(a -> {
		RiftPlaceholder placeholder = new RiftPlaceholder();
		placeholder.id = a;
		return placeholder;
	}, a -> a.id);

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
