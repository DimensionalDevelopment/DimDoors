package org.dimdev.dimdoors.rift.targets;

import net.minecraft.core.Rotations;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.Location;

import java.util.Collections;

public class UnstableTarget extends VirtualTarget implements EntityTarget {
	private static final RandomSource RANDOM = RandomSource.create();

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.UNSTABLE.get();
	}

	@Override
	public VirtualTarget copy() {
		return this;
	}

	@Override
	public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
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
					.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, location);
		}

		return LimboTarget.INSTANCE.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, location);
	}
}
