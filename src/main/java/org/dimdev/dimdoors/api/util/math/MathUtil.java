package org.dimdev.dimdoors.api.util.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.Random;

import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;

public final class MathUtil {
	public static <T> T weightedRandom(Map<T, Float> weights) {
		if (weights.size() == 0) return null;
		int totalWeight = 0;
		for (float weight : weights.values()) {
			totalWeight += weight;
		}
		Random random = new Random();
		float f = random.nextFloat() * totalWeight;
		for (Map.Entry<T, Float> e : weights.entrySet()) {
			f -= e.getValue();
			if (f < 0) return e.getKey();
		}
		return null;
	}

	public static EulerAngle eulerAngle(Vec3d direction, Vec3d upwards) {
		float pitch = pitch(direction);
		float yaw = yaw(direction);
		upwards = TransformationMatrix3d.builder().rotate(new EulerAngle(pitch, yaw, 0)).buildReverse().transform(upwards);
		float roll = (float) Math.toDegrees(-Math.atan2(upwards.x, upwards.y));

		return new EulerAngle(pitch, yaw, roll);
	}

	public static EulerAngle entityEulerAngle(Entity entity) {
		return new EulerAngle(entity.getPitch(), entity.getYaw(), 0);
	}

	public static float yaw(Vec3d vector) {
		return (float) Math.toDegrees(-Math.atan2(vector.x, vector.z));
	}

	public static float pitch(Vec3d vector) {
		return (float) Math.toDegrees(Math.asin(-vector.y));
	}

	public static EulerAngle directionEulerAngle(Direction direction) {
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
		DOWN(new EulerAngle(90, 0, 0)),
		UP(new EulerAngle(-90, 0, 0)),
		NORTH(new EulerAngle(0, -180, 0)),
		SOUTH(new EulerAngle(0, 0, 0)),
		WEST(new EulerAngle(0, 90, 0)),
		EAST(new EulerAngle(0, -90, 0));



		private final EulerAngle angle;

		EulerAngleDirection(EulerAngle angle) {
			this.angle = angle;
		}

		public EulerAngle getAngle() {
			return angle;
		}
	}
}
