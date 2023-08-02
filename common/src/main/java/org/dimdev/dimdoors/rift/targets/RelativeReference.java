package org.dimdev.dimdoors.rift.targets;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import org.dimdev.dimdoors.api.util.Location;

public class RelativeReference extends RiftReference {
	private final Vec3i offset;

	public RelativeReference(Vec3i offset) {
		this.offset = offset;
	}

	@Override
	public Location getReferencedLocation() {
		return new Location(this.location.world, this.location.pos.offset(this.offset));
	}

	public Vec3i getOffset() {
		return this.offset;
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.RELATIVE.get();
	}

	@Override
	public VirtualTarget copy() {
		return new RelativeReference(offset);
	}

	public static CompoundTag toNbt(RelativeReference target) {
		CompoundTag nbt = new CompoundTag();
		nbt.putIntArray("offset", new int[]{target.offset.getX(), target.offset.getY(), target.offset.getZ()});
		return nbt;
	}

	public static RelativeReference fromNbt(CompoundTag nbt) {
		int[] offset = nbt.getIntArray("offset");
		return new RelativeReference(new Vec3i(offset[0], offset[1], offset[2]));
	}
}
