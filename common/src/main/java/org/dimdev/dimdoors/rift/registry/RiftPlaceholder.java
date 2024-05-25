package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import org.dimdev.dimdoors.api.util.Location;

import java.util.UUID;

public class RiftPlaceholder extends Rift { // TODO: don't extend rift
	public static final MapCodec<RiftPlaceholder> CODEC = RecordCodecBuilder.mapCodec(inst -> commonFields(inst).apply(inst, RiftPlaceholder::new));

	public RiftPlaceholder(UUID id, Location location, boolean isDetached, LinkProperties properties) {
		super(id, location, isDetached, properties);
	}

	public RiftPlaceholder() {

	}

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
