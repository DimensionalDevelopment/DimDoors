package org.dimdev.dimdoors.rift.targets;

import org.dimdev.dimdoors.api.util.Location;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Vec3i;

public class RelativeReference extends RiftReference {
	private final Vec3i offset;

	public RelativeReference(Vec3i offset) {
		this.offset = offset;
	}

	@Override
	public Location getReferencedLocation() {
		return new Location(this.location.world, this.location.pos.add(this.offset));
	}

	public Vec3i getOffset() {
		return this.offset;
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.RELATIVE;
	}

	public static CompoundTag toTag(RelativeReference target) {
		CompoundTag tag = new CompoundTag();
		tag.putIntArray("offset", new int[]{target.offset.getX(), target.offset.getY(), target.offset.getZ()});
		return tag;
	}

	public static RelativeReference fromTag(CompoundTag tag) {
		int[] offset = tag.getIntArray("offset");
		return new RelativeReference(new Vec3i(offset[0], offset[1], offset[2]));
	}
}
