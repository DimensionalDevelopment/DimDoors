package org.dimdev.dimdoors.api.util.math;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

import org.dimdev.test.TestUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MathUtilTest {

	@Test
	public void eulerAngle() {
		EulerAngle expected = new EulerAngle(0, 0, 0);

		Vec3d direction = new Vec3d(0, 0, 1);
		Vec3d upwards = new Vec3d(0, 1, 0);
		EulerAngle angle = MathUtil.eulerAngle(direction, upwards);
		assertTrue(TestUtil.closeEnough(expected, angle), TestUtil.expectedActual(TestUtil.toString(expected), TestUtil.toString(angle)));

		expected = new EulerAngle(-90, 0, 0);
		direction = new Vec3d(0, 1, 0);
		upwards = new Vec3d(0, 0, -1);
		angle = MathUtil.eulerAngle(direction, upwards);
		assertTrue(TestUtil.closeEnough(expected, angle), TestUtil.expectedActual(TestUtil.toString(expected), TestUtil.toString(angle)));

		expected = new EulerAngle(0, -45, 0);
		direction = new Vec3d(Math.cos(Math.PI / 2), 0, Math.cos(Math.PI / 2));
		upwards = new Vec3d(0, 1, 0);
		angle = MathUtil.eulerAngle(direction, upwards);
		assertTrue(TestUtil.closeEnough(expected, angle), TestUtil.expectedActual(TestUtil.toString(expected), TestUtil.toString(angle)));
	}

	@Test
	public void directionEulerAngle() {
		for (Direction direction : Direction.values()) {
			EulerAngle expected = MathUtil.directionEulerAngle(direction);
			Vec3d dir = Vec3d.of(direction.getVector());
			EulerAngle angle = new EulerAngle(MathUtil.pitch(dir), MathUtil.yaw(dir), 0);

			assertTrue(TestUtil.closeEnough(expected, angle), TestUtil.expectedActual(TestUtil.toString(expected), TestUtil.toString(angle)));
		}
	}
}
