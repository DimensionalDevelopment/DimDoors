package org.dimdev.dimdoors.api.util;

import java.util.Random;

import org.junit.jupiter.api.Test;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.border.WorldBorder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeleportUtilTest {
	private static final int CENTER_CASES = 15;
	private static final int CLAMP_CASES = 25;

	@Test
	public void clampToWorldBorder() {
		Random random = new Random(33554432);
		int[] xCenters = new int[CENTER_CASES];
		int[] zCenters = new int[CENTER_CASES];
		for (int i = 0; i < CENTER_CASES; i++) {
			xCenters[i] = random.nextInt(1000, 2000) * (random.nextBoolean() ? 1 : -1);
			zCenters[i] = random.nextInt(1000, 2000) * (random.nextBoolean() ? 1 : -1);
		}
		for (int i = 1; i <= CENTER_CASES; i++) {
			WorldBorder border = new WorldBorder();
			border.setCenter(xCenters[i - 1], zCenters[i - 1]);
			int size = 100 * i * i * i;
			border.setSize(size);
			for (int j = 0; j < CLAMP_CASES; j++) {
				int xValue;
				if (border.getCenterX() < 0) {
					xValue = (int) border.getCenterX() - random.nextInt(0, 1048576);
				} else {
					xValue = (int) border.getCenterX() + random.nextInt(0, 1048576);
				}
				int zValue;
				if (border.getCenterZ() < 0) {
					zValue = (int) border.getCenterZ() - random.nextInt(1000000, 10000000);
				} else {
					zValue = (int) border.getCenterZ() + random.nextInt(1000000, 10000000);
				}
				Vec3d oobVector = new Vec3d(xValue, 0, zValue);
				Vec3d clampedVector = TeleportUtil.clampToWorldBorder(oobVector, border);
				assertFalse(border.contains(oobVector.x, oobVector.z));
				assertTrue(border.contains(clampedVector.x, clampedVector.z));
			}
		}
	}
}
