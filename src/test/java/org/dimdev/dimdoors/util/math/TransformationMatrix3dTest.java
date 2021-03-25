package org.dimdev.dimdoors.util.math;

import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.test.TestUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransformationMatrix3dTest {

	@Test
	void identity() {
		double[][] matrix = new double[4][4];
		matrix[0] = new double[]{1, 0, 0, 0};
		matrix[1] = new double[]{0, 1, 0, 0};
		matrix[2] = new double[]{0, 0, 1, 0};
		matrix[3] = new double[]{0, 0, 0, 1};
		TransformationMatrix3d identity = new TransformationMatrix3d(matrix);
		assertEquals(identity, TransformationMatrix3d.identity());

		matrix[3] = new double[]{1, 0, 0, 1};
		TransformationMatrix3d matrix3d = new TransformationMatrix3d(matrix);
		assertNotEquals(matrix3d, TransformationMatrix3d.identity());
	}

	@Test
	void transformVec3d() {
		// rotate around
		Vec3d vector = new Vec3d(1, 0, 0);
		Vec3d expected;
		TransformationMatrix3d rotate90DegreesY = TransformationMatrix3d.builder().rotateY(Math.PI / 2).build();

		vector = rotate90DegreesY.transform(vector);
		expected = new Vec3d(0, 0, -1);
		assertTrue(TestUtil.closeEnough(expected, vector), TestUtil.expectedActual(expected, vector));

		vector = rotate90DegreesY.transform(vector);
		expected = new Vec3d(-1, 0, 0);
		assertTrue(TestUtil.closeEnough(expected, vector), TestUtil.expectedActual(expected, vector));

		vector = rotate90DegreesY.transform(vector);
		expected = new Vec3d(0, 0, 1);
		assertTrue(TestUtil.closeEnough(expected, vector), TestUtil.expectedActual(expected, vector));

		vector = rotate90DegreesY.transform(vector);
		expected = new Vec3d(1, 0, 0);
		assertTrue(TestUtil.closeEnough(expected, vector), TestUtil.expectedActual(expected, vector));

		TransformationMatrix3d rotate45DegreesY = TransformationMatrix3d.builder().rotateY(Math.PI / 4).build();

		vector = rotate45DegreesY.transform(vector);
		expected = new Vec3d(Math.cos(Math.PI/4), 0, -Math.sin(Math.PI/4));
		assertTrue(TestUtil.closeEnough(expected, vector), TestUtil.expectedActual(expected, vector));

		double random = Math.random()*2*Math.PI;
		expected = new Vec3d(Math.cos(random), 0, -Math.sin(random));
		TransformationMatrix3d.TransformationMatrix3dBuilder builder = TransformationMatrix3d.builder()
				.rotate(new EulerAngle((((float) Math.random()) - 0.5F) * 180, (((float) Math.random()) - 0.5F) * 360, (((float) Math.random()) - 0.5F) * 360))
				.translate(new Vec3d(Math.random()*100, Math.random()*100, Math.random()*100))
				.rotate(new EulerAngle((((float) Math.random()) - 0.5F) * 180, (((float) Math.random()) - 0.5F) * 360, (((float) Math.random()) - 0.5F) * 360))
				.translate(new Vec3d(Math.random()*100, Math.random()*100, Math.random()*100));

		vector = builder.buildReverse().transform(builder.build().transform(expected));
		assertTrue(TestUtil.closeEnough(expected, vector), TestUtil.expectedActual(expected, vector));

	}

	@Test
	void transformEulerAngle() {
		EulerAngle expected;
		EulerAngle angle;

		TransformationMatrix3d identity = TransformationMatrix3d.identity();

		expected = new EulerAngle(0, 0, 0);
		angle = identity.transform(expected);
		assertTrue(TestUtil.closeEnough(expected, angle), TestUtil.expectedActual(TestUtil.toString(expected), TestUtil.toString(angle)));

		expected = new EulerAngle(90, 0, 0);
		angle = identity.transform(expected);
		assertTrue(TestUtil.closeEnough(expected, angle), TestUtil.expectedActual(TestUtil.toString(expected), TestUtil.toString(angle)));

		expected = new EulerAngle(0, 90, 0);
		angle = identity.transform(expected);
		assertTrue(TestUtil.closeEnough(expected, angle), TestUtil.expectedActual(TestUtil.toString(expected), TestUtil.toString(angle)));

		expected = new EulerAngle(0, 0, 90);
		angle = identity.transform(expected);
		assertTrue(TestUtil.closeEnough(expected, angle), TestUtil.expectedActual(TestUtil.toString(expected), TestUtil.toString(angle)));

		expected = new EulerAngle(90, 90, 90);
		angle = identity.transform(expected);
		assertTrue(TestUtil.closeEnough(expected, angle), TestUtil.expectedActual(TestUtil.toString(expected), TestUtil.toString(angle)));

		// randomize EulerAngle
		expected = new EulerAngle((((float) Math.random()) - 0.5F) * 180, (((float) Math.random()) - 0.5F) * 360, (((float) Math.random()) - 0.5F) * 360);

		angle = identity.transform(expected);
		assertTrue(TestUtil.closeEnough(expected, angle), TestUtil.expectedActual(TestUtil.toString(expected), TestUtil.toString(angle)));

		angle = expected;
		TransformationMatrix3d rotate90DegreesY = TransformationMatrix3d.builder().rotateY(Math.PI / 2).build();
		for (int i = 0; i < 4; i++) {
			angle = rotate90DegreesY.transform(angle);
		}
		assertTrue(TestUtil.closeEnough(expected, angle), TestUtil.expectedActual(TestUtil.toString(expected), TestUtil.toString(angle)));

		angle = expected;
		TransformationMatrix3d rotate90DegreesX = TransformationMatrix3d.builder().rotateX(Math.PI / 2).build();
		for (int i = 0; i < 4; i++) {
			angle = rotate90DegreesX.transform(angle);
		}
		assertTrue(TestUtil.closeEnough(expected, angle), TestUtil.expectedActual(TestUtil.toString(expected), TestUtil.toString(angle)));

		angle = expected;
		TransformationMatrix3d rotate90DegreesZ = TransformationMatrix3d.builder().rotateZ(Math.PI / 2).build();
		for (int i = 0; i < 4; i++) {
			angle = rotate90DegreesZ.transform(angle);
		}
		assertTrue(TestUtil.closeEnough(expected, angle), TestUtil.expectedActual(TestUtil.toString(expected), TestUtil.toString(angle)));
	}

	@Test
	void product() {
		// compare I and I^2
		assertEquals(TransformationMatrix3d.identity(), TransformationMatrix3d.identity().product(TransformationMatrix3d.identity()));
	}
}
