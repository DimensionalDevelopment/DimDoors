package org.dimdev.dimdoors.shared.world.limbodimension;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.world.CustomSkyProvider;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Jared Johnson on 1/24/2017.
 */
public class LimboSkyProvider extends CustomSkyProvider {

    private static final ResourceLocation moonRenderPath = new ResourceLocation(DimDoors.MODID + ":textures/other/limbo_moon.png");
    private static final ResourceLocation sunRenderPath = new ResourceLocation(DimDoors.MODID + ":textures/other/limbo_sun.png");

    @Override
    public ResourceLocation getMoonRenderPath() {
        return moonRenderPath;
    }

    @Override
    public ResourceLocation getSunRenderPath() {
        return sunRenderPath;
    }
}
