package org.dimdev.dimdoors.rift.targets;

import java.util.Collections;
import java.util.Random;

import org.dimdev.dimdoors.api.rift.target.EntityTarget;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

public class UnstableTarget extends VirtualTarget implements EntityTarget {
	private static final Random RANDOM = new Random();

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.UNSTABLE;
	}

	@Override
	public boolean receiveEntity(Entity entity, Vec3d relativePos, EulerAngle relativeAngle, Vec3d relativeVelocity) {
		if (RANDOM.nextBoolean()) {
			return RandomTarget.builder()
					.acceptedGroups(Collections.singleton(0))
					.coordFactor(1)
					.negativeDepthFactor(10000)
					.positiveDepthFactor(80)
					.weightMaximum(100)
					.noLink(false)
					.noLinkBack(false)
					.newRiftWeight(1)
					.build()
					.as(Targets.ENTITY)
					.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity);
		}

		return LimboTarget.INSTANCE.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity);
	}
}
