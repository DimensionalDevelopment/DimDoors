package org.dimdev.dimdoors.api.client;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.dimdev.dimdoors.api.client.util.GlUtils;

import java.util.Objects;

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

    private void _blitToScreen(int i, int j, boolean bl) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._colorMask(true, true, true, false);
        GlStateManager._disableDepthTest();
        GlStateManager._depthMask(false);
        GlStateManager._viewport(0, 0, i, j);
        if (bl) {
            GlStateManager._disableBlend();
        }

        Minecraft minecraft = Minecraft.getInstance();
        ShaderInstance shaderInstance = (ShaderInstance) Objects.requireNonNull(minecraft.gameRenderer.blitShader, "Blit shader not loaded");
        shaderInstance.setSampler("DiffuseSampler", dimensionalPortalRenderTarget.getColorTextureId());
        shaderInstance.apply();
        BufferBuilder bufferBuilder = RenderSystem.renderThreadTesselator().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLIT_SCREEN);
        bufferBuilder.addVertex(0.0F, 0.0F, 0.0F);
        bufferBuilder.addVertex(1.0F, 0.0F, 0.0F);
        bufferBuilder.addVertex(1.0F, 1.0F, 0.0F);
        bufferBuilder.addVertex(0.0F, 1.0F, 0.0F);
        BufferUploader.draw(bufferBuilder.buildOrThrow());
        shaderInstance.clear();
        GlStateManager._depthMask(true);
        GlStateManager._colorMask(true, true, true, true);
    }

    public static void init() {
        var window = Minecraft.getInstance().getWindow();

        dimensionalPortalRenderTarget = new MainTarget(window.getScreenWidth(), window.getScreenHeight());
        dimensionalPortalRenderTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        dimensionalPortalRenderTarget.clear(ON_OSX);

        setup = true;
    }
}
