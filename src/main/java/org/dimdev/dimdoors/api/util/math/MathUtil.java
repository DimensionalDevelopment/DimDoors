package org.dimdev.dimdoors.api.util.math;

import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class MathUtil {
	public static <T> T weightedRandom(Map<T, Float> weights) {
		if (weights.size() == 0) return null;
		int totalWeight = 0;
		for (float weight : weights.values()) {
			totalWeight += weight;
		}
		RandomSource random = RandomSource.create();
		float f = random.nextFloat() * totalWeight;
		for (Map.Entry<T, Float> e : weights.entrySet()) {
			f -= e.getValue();
			if (f < 0) return e.getKey();
		}
		return null;
	}

	public static Rotations eulerAngle(Vec3 direction, Vec3 upwards) {
		float pitch = pitch(direction);
		float yaw = yaw(direction);
		upwards = TransformationMatrix3d.builder().rotate(new Rotations(pitch, yaw, 0)).buildReverse().transform(upwards);
		float roll = (float) Math.toDegrees(-Math.atan2(upwards.x, upwards.y));

		return new Rotations(pitch, yaw, roll);
	}

	public static Rotations entityEulerAngle(Entity entity) {
		return new Rotations(entity.getXRot(), entity.getYRot(), 0);
	}

	public static float yaw(Vec3 vector) {
		return (float) Math.toDegrees(-Math.atan2(vector.x, vector.z));
	}

	public static float pitch(Vec3 vector) {
		return (float) Math.toDegrees(Math.asin(-vector.y));
	}

	public static Rotations directionEulerAngle(Direction direction) {
		switch (direction) {
			case DOWN:
				return EulerAngleDirection.DOWN.getAngle();
			case UP:
				return EulerAngleDirection.UP.getAngle();
			case NORTH:
				return EulerAngleDirection.NORTH.getAngle();
			case SOUTH:
				return EulerAngleDirection.SOUTH.getAngle();
			case WEST:
				return EulerAngleDirection.WEST.getAngle();
			case EAST:
			default:
				return EulerAngleDirection.EAST.getAngle();
		}
	}

	public enum EulerAngleDirection {
		DOWN(new Rotations(90, 0, 0)),
		UP(new Rotations(-90, 0, 0)),
		NORTH(new Rotations(0, -180, 0)),
		SOUTH(new Rotations(0, 0, 0)),
		WEST(new Rotations(0, 90, 0)),
		EAST(new Rotations(0, -90, 0));



		private final Rotations angle;

		EulerAngleDirection(Rotations angle) {
			this.angle = angle;
		}

		public Rotations getAngle() {
			return angle;
		}
	}
}
