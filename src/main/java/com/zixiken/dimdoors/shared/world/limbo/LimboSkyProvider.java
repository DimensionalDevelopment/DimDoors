package com.zixiken.dimdoors.shared.world.limbo;

import com.zixiken.dimdoors.shared.world.CustomSkyProvider;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Jared Johnson on 1/24/2017.
 */
public class LimboSkyProvider extends CustomSkyProvider
{
    @Override
    public ResourceLocation getMoonRenderPath()
    {
        return new ResourceLocation("DimDoors:textures/other/limboMoon.png");
    }

    @Override
    public ResourceLocation getSunRenderPath() {
        return new ResourceLocation("DimDoors:textures/other/limboSun.png");
    }
}