package org.dimdev.dimdoors.world.limbo;

import org.dimdev.dimdoors.world.CustomSkyProvider;
import net.minecraft.util.Identifier;

/**
 * Created by Jared Johnson on 1/24/2017.
 */
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
