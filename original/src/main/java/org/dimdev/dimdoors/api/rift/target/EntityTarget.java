package org.dimdev.dimdoors.api.rift.target;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

public interface EntityTarget extends Target {
	boolean receiveEntity(Entity entity, Vec3d relativePos, EulerAngle relativeAngle, Vec3d relativeVelocity);
}
