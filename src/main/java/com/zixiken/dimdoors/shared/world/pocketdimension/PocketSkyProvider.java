package com.zixiken.dimdoors.shared.world.pocketdimension;

import com.zixiken.dimdoors.shared.world.CustomSkyProvider;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Jared Johnson on 1/24/2017.
 */
public class PocketSkyProvider extends CustomSkyProvider {

    @Override
    public ResourceLocation getMoonRenderPath() {
        return new ResourceLocation("DimDoors:textures/other/limbo_moon.png");
    }

    @Override
    public ResourceLocation getSunRenderPath() {
        return new ResourceLocation("DimDoors:textures/other/limbo_sun.png");
    }
}
