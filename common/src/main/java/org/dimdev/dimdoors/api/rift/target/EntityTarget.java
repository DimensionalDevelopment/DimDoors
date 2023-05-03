package org.dimdev.dimdoors.api.rift.target;

import net.minecraft.core.Rotations;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface EntityTarget extends Target {
	boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity);
}
