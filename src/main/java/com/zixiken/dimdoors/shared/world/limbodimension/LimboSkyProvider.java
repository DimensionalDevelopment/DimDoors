package com.zixiken.dimdoors.shared.world.limbodimension;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.world.CustomSkyProvider;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Jared Johnson on 1/24/2017.
 */
public class LimboSkyProvider extends CustomSkyProvider {
    private static final ResourceLocation limboMoonResourceLoc = new ResourceLocation(DimDoors.MODID + ":textures/other/limbo_moon.png");
    private static final ResourceLocation limboSunResourceLoc = new ResourceLocation(DimDoors.MODID + ":textures/other/limbo_sun.png");

    @Override
    public ResourceLocation getMoonRenderPath() {
        return limboMoonResourceLoc;
    }

    @Override
    public ResourceLocation getSunRenderPath() {
        return limboSunResourceLoc;
    }


}
