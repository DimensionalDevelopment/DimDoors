package org.dimdev.dimdoors.api.util.math;

import org.dimdev.test.TestUtil;
import org.junit.jupiter.api.Test;

import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.world.phys.Vec3;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MathUtilTest {

	@Test
	public void eulerAngle() {
		Rotations expected = new Rotations(0, 0, 0);

		Vec3 direction = new Vec3(0, 0, 1);
		Vec3 upwards = new Vec3(0, 1, 0);
		Rotations angle = MathUtil.eulerAngle(direction, upwards);
		assertTrue(TestUtil.closeEnough(expected, angle), TestUtil.expectedActual(TestUtil.toString(expected), TestUtil.toString(angle)));

		expected = new Rotations(-90, 0, 0);
		direction = new Vec3(0, 1, 0);
		upwards = new Vec3(0, 0, -1);
		angle = MathUtil.eulerAngle(direction, upwards);
		assertTrue(TestUtil.closeEnough(expected, angle), TestUtil.expectedActual(TestUtil.toString(expected), TestUtil.toString(angle)));

		expected = new Rotations(0, -45, 0);
		direction = new Vec3(Math.cos(Math.PI / 2), 0, Math.cos(Math.PI / 2));
		upwards = new Vec3(0, 1, 0);
		angle = MathUtil.eulerAngle(direction, upwards);
		assertTrue(TestUtil.closeEnough(expected, angle), TestUtil.expectedActual(TestUtil.toString(expected), TestUtil.toString(angle)));
	}

	@Test
	public void directionEulerAngle() {
		for (Direction direction : Direction.values()) {
			Rotations expected = MathUtil.directionEulerAngle(direction);
			Vec3 dir = Vec3.atLowerCornerOf(direction.getNormal());
			Rotations angle = new Rotations(MathUtil.pitch(dir), MathUtil.yaw(dir), 0);

			assertTrue(TestUtil.closeEnough(expected, angle), TestUtil.expectedActual(TestUtil.toString(expected), TestUtil.toString(angle)));
		}
	}
}
