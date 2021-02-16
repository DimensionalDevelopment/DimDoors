package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import org.dimdev.dimdoors.util.TeleportUtil;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.entity.Entity;

public class LimboTarget extends VirtualTarget implements EntityTarget {
	public static final LimboTarget INSTANCE = new LimboTarget();
	public static final Codec<LimboTarget> CODEC = Codec.unit(INSTANCE);

	private LimboTarget() {
	}

	@Override
	public boolean receiveEntity(Entity entity, Vec3d relativePos, EulerAngle relativeAngle, Vec3d relativeVelocity) {
		TeleportUtil.teleport(entity, ModDimensions.LIMBO_DIMENSION, entity.getPos(), relativeAngle);
		return true;
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.LIMBO;
	}
}
