package org.dimdev.dimdoors.client.dimensioneffects;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;

public class BaseDimensonSpecialEffects extends DimensionSpecialEffects {
	public BaseDimensonSpecialEffects(boolean pHasGround) {
		super(Float.NaN, pHasGround, SkyType.NONE, false, false); //figure out if we need to change this/
	}

	@Override
	public Vec3 getBrightnessDependentFogColor(Vec3 pFogColor, float pBrightness) {
		return null;
	}

	@Override
	public boolean isFoggyAt(int pX, int pY) {
		return false;
	}
}
