package org.dimdev.dimdoors.client;

import net.fabricmc.api.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.ShaderInstance;

@OnlyIn(Dist.CLIENT)
public class ModShaders {
	private static ShaderInstance DIMENSIONAL_PORTAL = null;

	public static void setDimensionalPortal(ShaderInstance dimensionalPortal) {
		DIMENSIONAL_PORTAL = dimensionalPortal;
	}

	public static ShaderInstance getDimensionalPortal() {
		return DIMENSIONAL_PORTAL;
	}
}
