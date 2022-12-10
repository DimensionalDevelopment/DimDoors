package org.dimdev.dimdoors.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.ShaderProgram;

@Environment(EnvType.CLIENT)
public class ModShaders {
	private static ShaderProgram DIMENSIONAL_PORTAL = null;

	public static void setDimensionalPortal(ShaderProgram dimensionalPortal) {
		DIMENSIONAL_PORTAL = dimensionalPortal;
	}

	public static ShaderProgram getDimensionalPortal() {
		return DIMENSIONAL_PORTAL;
	}
}
