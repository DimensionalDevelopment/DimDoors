package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;
import org.dimdev.dimdoors.PortalColors;
import org.dimdev.dimdoors.api.client.UniformExt;

public class ModShaders {
    private static Uniform COLORS;
    private static ShaderInstance DIMENSIONAL_PORTAL = null;

    public static void setDimensionalPortal(ShaderInstance dimensionalPortal) {
        DIMENSIONAL_PORTAL = dimensionalPortal;

        COLORS = dimensionalPortal.getUniform("Colors");
        if(COLORS != null) ((UniformExt) COLORS).dimensionalDoors$set(PortalColors.base());
    }

    public static ShaderInstance getDimensionalPortal() {
        return DIMENSIONAL_PORTAL;
    }

    public static boolean setPortalColors(int[] colors) {
        if(COLORS != null) {
            ((UniformExt) COLORS).dimensionalDoors$set(colors);
            return true;
        }

        return false;
    }

    public static boolean getPortalColors(int[] target) {
        if(COLORS != null) {
            COLORS.getIntBuffer().get(0, target, 0, target.length);

            return true;
        }

        return false;
    }
}
