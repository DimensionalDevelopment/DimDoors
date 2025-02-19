package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

public class RiftPlaceholder extends Rift { // TODO: don't extend rift
	public final static MapCodec<RiftPlaceholder> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			UUIDUtil.CODEC.fieldOf("id").forGetter(RiftPlaceholder::getId)
	).apply(instance, id -> {
		var placeholder = new RiftPlaceholder();
		placeholder.setId(id);
		return placeholder;
	}));

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
}