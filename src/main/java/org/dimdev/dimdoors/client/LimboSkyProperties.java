package org.dimdev.dimdoors.client;

import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.math.Vec3d;

public class LimboSkyProperties extends SkyProperties {
    public LimboSkyProperties() {
        super(Float.NaN, true, SkyType.NORMAL, true, true);
    }

    @Override
    public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
        return color;
    }

    @Override
    public boolean useThickFog(int camX, int camY) {
        return false;
    }
}
