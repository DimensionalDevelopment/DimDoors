package org.dimdev.dimdoors.client;

import net.minecraft.class_5944;

public class ModShaders {
	private static class_5944 DIMENSIONAL_PORTAL = null;

	public static void setDimensionalPortal(class_5944 dimensionalPortal) {
		DIMENSIONAL_PORTAL = dimensionalPortal;
	}

	public static class_5944 getDimensionalPortal() {
		return DIMENSIONAL_PORTAL;
	}
}
