package org.dimdev.test;

import net.minecraft.core.Rotations;
import net.minecraft.world.phys.Vec3;

import org.dimdev.dimdoors.api.util.math.AbstractMatrixd;

public class TestUtil {
	public static String expectedActual(Object expected,Object actual) {
		return "\nexpected:\n" + expected + "\nactual:\n" + actual + "\n";
	}

	public static boolean closeEnough(Vec3 expected, Vec3 actual) {
		return expected.distanceToSqr(actual) <= expected.lengthSqr() * 1E-10;
	}

	public static boolean closeEnough(AbstractMatrixd<?> expected, AbstractMatrixd<?> actual) {
		if (expected.getDimensionX() != actual.getDimensionX() || expected.getDimensionY() != actual.getDimensionY()) return false;

		for (int i = 0; i < expected.getDimensionX(); i++) {
			for (int j = 0; j < expected.getDimensionY(); j++) {
				double entry1 = expected.get(i, j);
				double entry2 = actual.get(i, j);
				if (entry1 == entry2) continue;
				if (entry1 != 0 && entry2 != 0) {
					double div = entry1/entry2;
					if (0.999 <= div && div <= 1.001) continue;
				}
				if (Math.abs(entry1 - entry2) <= 1E-10) continue;
				return false;
			}
		}
		return true;
	}

	public static boolean closeEnough(Rotations expected, Rotations actual) {
		float yawDiff = Math.abs(expected.getY() - actual.getY());
		float pitchDiff = Math.abs(expected.getX() - actual.getX());
		float rollDiff = Math.abs(expected.getZ() - actual.getZ());

		return yawDiff <= 1 && pitchDiff <= 1 && rollDiff <= 1;
	}

	public static String toString(Rotations angle) {
		return "{yaw: " + angle.getY() + "; pitch: " + angle.getX() + "; roll: " + angle.getZ() + "}";
	}
}
