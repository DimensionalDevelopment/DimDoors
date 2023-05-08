package org.dimdev.dimdoors.rift.targets;

import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.EntityUtils;

public class IdMarker extends VirtualTarget implements EntityTarget {
	private final int id;

	public IdMarker(int id) {
		this.id = id;
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.ID_MARKER.get();
	}

	public static CompoundTag toNbt(IdMarker target) {
		CompoundTag nbt = new CompoundTag();
		nbt.putInt("id", target.id);
		return nbt;
	}

	public static IdMarker fromNbt(CompoundTag nbt) {
		return new IdMarker(nbt.getInt("id"));
	}

	public int getId() {
		return this.id;
	}

	@Override
	public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity) {
		EntityUtils.chat(entity, Component.literal("This rift is configured for pocket dungeons. Its id is " + this.id));
		return false;
	}
}
