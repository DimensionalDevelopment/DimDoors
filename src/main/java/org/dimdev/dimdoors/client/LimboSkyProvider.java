package org.dimdev.dimdoors.client;

import net.minecraft.util.Identifier;

public class LimboSkyProvider extends CustomSkyProvider {

    private static final Identifier moonRenderPath = new Identifier("dimdoors:textures/other/limbo_moon.png");
    private static final Identifier sunRenderPath = new Identifier("dimdoors:textures/other/limbo_sun.png");

    @Override
    public Identifier getMoonRenderPath() {
        return moonRenderPath;
    }

    @Override
    public Identifier getSunRenderPath() {
        return sunRenderPath;
    }
}