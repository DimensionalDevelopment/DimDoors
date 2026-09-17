package org.dimdev.dimdoors.client.effect;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;

public class VoidDimensionSpecialEffects extends DimensionSpecialEffects {
    public VoidDimensionSpecialEffects() {
        super(-30, false, SkyType.NONE, false, true);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
        return fogColor;
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return false;
    }
}
