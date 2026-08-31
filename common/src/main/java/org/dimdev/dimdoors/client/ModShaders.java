package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;
import org.lwjgl.opengl.GL33;

public class ModShaders {
    private static ShaderInstance DIMENSIONAL_PORTAL = null;
    private static int programId;

    public static void setDimensionalPortal(ShaderInstance dimensionalPortal) {
        DIMENSIONAL_PORTAL = dimensionalPortal;
    }

    public static int getTypeFromString(String typeName) {
        int i = -1;
        if ("int".equals(typeName)) {
            i = 0;
        } else if ("float".equals(typeName)) {
            i = 4;
        } else if (typeName.startsWith("matrix")) {
            if (typeName.endsWith("2x2")) {
                i = 8;
            } else if (typeName.endsWith("3x3")) {
                i = 9;
            } else if (typeName.endsWith("4x4")) {
                i = 10;
            }
        }

        return i;
    }


    public static ShaderInstance getDimensionalPortal() {
        return DIMENSIONAL_PORTAL;
    }
}
