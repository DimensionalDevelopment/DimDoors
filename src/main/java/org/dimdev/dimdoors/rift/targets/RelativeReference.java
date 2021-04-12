package org.dimdev.dimdoors.rift.targets;

import org.dimdev.dimdoors.api.util.Location;
import net.minecraft.nbt.NbtCompound;
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

	public static NbtCompound toNbt(RelativeReference target) {
		NbtCompound nbt = new NbtCompound();
		nbt.putIntArray("offset", new int[]{target.offset.getX(), target.offset.getY(), target.offset.getZ()});
		return nbt;
	}

	public static RelativeReference fromNbt(NbtCompound nbt) {
		int[] offset = nbt.getIntArray("offset");
		return new RelativeReference(new Vec3i(offset[0], offset[1], offset[2]));
	}
}
