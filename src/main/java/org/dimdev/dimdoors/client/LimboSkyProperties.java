package org.dimdev.dimdoors.client;

import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.math.Vec3d;

public class LimboSkyProperties extends SkyProperties {
    public static final LimboSkyProperties INSTANCE = new LimboSkyProperties();

    private LimboSkyProperties() {
        super(Float.NaN, false, SkyProperties.SkyType.NORMAL, true, true);
    }

    @Override
    public Vec3d adjustSkyColor(Vec3d color, float sunHeight) {
        return color.multiply(0.15000000596046448D);
    }

    @Override
    public boolean useThickFog(int camX, int camY) {
        return true;
    }

    @Override
    public float[] getSkyColor(float skyAngle, float tickDelta) {
        return new float[]{0, 0, 0, 0};
    }
}
