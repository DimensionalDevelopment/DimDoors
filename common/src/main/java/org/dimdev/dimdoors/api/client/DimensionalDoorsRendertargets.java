package org.dimdev.dimdoors.api.client;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;

import static net.minecraft.client.Minecraft.ON_OSX;

public class DimensionalDoorsRendertargets {
    private static boolean setup = false;

    private static MainTarget dimensionalPortalRenderTarget;

    public static RenderTarget getDimensionalPortalRenderTarget() {
        return dimensionalPortalRenderTarget;
    }

    public static void resize(int width, int height) {
        if(!setup) init();

        dimensionalPortalRenderTarget.resize(width, height, ON_OSX);
    }

    public static void renderToScreen() {
//        GlUtils.copyColorFrom(Minecraft.getInstance().getMainRenderTarget(), dimensionalPortalRenderTarget);

        dimensionalPortalRenderTarget.blitToScreen(Minecraft.getInstance().getWindow().getScreenWidth(), Minecraft.getInstance().getWindow().getScreenHeight() );
        dimensionalPortalRenderTarget.clear(true);
    }

    public static void init() {
        var window = Minecraft.getInstance().getWindow();

        dimensionalPortalRenderTarget = new MainTarget(window.getScreenWidth(), window.getScreenHeight());
        dimensionalPortalRenderTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        dimensionalPortalRenderTarget.clear(ON_OSX);

        setup = true;
    }
}
