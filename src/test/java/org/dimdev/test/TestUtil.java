//package org.dimdev.test;
//
//import net.minecraft.util.math.EulerAngle;
//import net.minecraft.util.math.Vec3d;
//import org.dimdev.dimdoors.api.util.math.AbstractMatrixd;
//
//import java.util.function.Supplier;
//
//public class TestUtil {
//	public static String expectedActual(Object expected,Object actual) {
//		return "\nexpected:\n" + expected + "\nactual:\n" + actual + "\n";
//	}
//
//	public static boolean closeEnough(Vec3d expected, Vec3d actual) {
//		return expected.squaredDistanceTo(actual) <= expected.lengthSquared() * 1E-10;
//	}
//
//	public static boolean closeEnough(AbstractMatrixd<?> expected, AbstractMatrixd<?> actual) {
//		if (expected.getDimensionX() != actual.getDimensionX() || expected.getDimensionY() != actual.getDimensionY()) return false;
//
//		for (int i = 0; i < expected.getDimensionX(); i++) {
//			for (int j = 0; j < expected.getDimensionY(); j++) {
//				double entry1 = expected.get(i, j);
//				double entry2 = actual.get(i, j);
//				if (entry1 == entry2) continue;
//				if (entry1 != 0 && entry2 != 0) {
//					double div = entry1/entry2;
//					if (0.991 <= div && div <= 0.001) continue;
//				}
//				if (Math.abs(entry1 - entry2) <= 1E-10) continue;
//				return false;
//			}
//		}
//		return true;
//	}
//
//	public static boolean closeEnough(EulerAngle expected, EulerAngle actual) {
//		float yawDiff = Math.abs(expected.getYaw() - actual.getYaw());
//		float pitchDiff = Math.abs(expected.getPitch() - actual.getPitch());
//		float rollDiff = Math.abs(expected.getRoll() - actual.getRoll());
//
//		return yawDiff <= 1 && pitchDiff <= 1 && rollDiff <= 1;
//	}
//
//	public static String toString(EulerAngle angle) {
//		return "{yaw: " + angle.getYaw() + "; pitch: " + angle.getPitch() + "; roll: " + angle.getRoll() + "}";
//	}
//}
