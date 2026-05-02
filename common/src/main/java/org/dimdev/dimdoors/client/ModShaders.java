package org.dimdev.dimdoors.client;

import net.minecraft.client.renderer.ShaderInstance;

public class ModShaders {
    private static ShaderInstance DIMENSIONAL_PORTAL = null;

    public static void setDimensionalPortal(ShaderInstance dimensionalPortal) {
        DIMENSIONAL_PORTAL = dimensionalPortal;
    }

    public static ShaderInstance getDimensionalPortal() {
    return DIMENSIONAL_PORTAL;
    }
}
