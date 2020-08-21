package org.dimdev.dimdoors.client;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;

public class LimboSkyProvider extends CustomSkyProvider {

    private static final Identifier MOON_RENDER_PATH = new Identifier("dimdoors:textures/other/limbo_moon.png");
    private static final Identifier SUN_RENDER_PATH = new Identifier("dimdoors:textures/other/limbo_sun.png");

    public LimboSkyProvider() {
        super(MOON_RENDER_PATH, SUN_RENDER_PATH, new Vec3i(255, 255, 255));
    }
}