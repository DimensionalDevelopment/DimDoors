package org.dimdev.dimdoors.client.effect.sky;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.client.CloudRenderBuffer;
import org.dimdev.dimdoors.client.effect.LevelRendererExtension;
import org.dimdev.dimdoors.world.pocket.type.addon.cloud.OverworldCloudData;
import org.dimdev.dimdoors.world.pocket.type.addon.sky.OverWorldSkyData;
import org.dimdev.dimdoors.world.pocket.type.addon.sky.OverworldWeatherData;
import org.joml.Matrix4f;

import static net.minecraft.client.renderer.LevelRenderer.MOON_LOCATION;
import static net.minecraft.client.renderer.LevelRenderer.SUN_LOCATION;

public class OverworldEnvironmentRendering {
    public static void renderSky(OverWorldSkyData info, ClientLevel level, PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, boolean isFoggy, Runnable skyFogSetup, Camera camera) {
        var minecraft = Minecraft.getInstance();
        var levelRenderer = minecraft.levelRenderer;

        Vec3 vec3 = info.getCorrectedSkyColor();
        float g = (float) vec3.x;
        float h = (float) vec3.y;
        float i = (float) vec3.z;
        FogRenderer.levelFogColor();
        Tesselator tesselator = Tesselator.getInstance();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(g, h, i, 1.0F);
        ShaderInstance shaderInstance = RenderSystem.getShader();
        levelRenderer.skyBuffer.bind();
        levelRenderer.skyBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderInstance);
        VertexBuffer.unbind();
        RenderSystem.enableBlend();
        float[] fs = info.getSunriseColor(info.getTimeOfDay());
        if (fs != null) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.pushPose();
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F));
            float j = Mth.sin(info.getSunAngle()) < 0.0F ? 180.0F : 0.0F;
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(j));
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F));
            float k = fs[0];
            float l = fs[1];
            float m = fs[2];
            Matrix4f matrix4f3 = poseStack.last().pose();
            BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
            bufferBuilder.addVertex(matrix4f3, 0.0F, 100.0F, 0.0F).setColor(k, l, m, fs[3]);
            int n = 16;

            for (int o = 0; o <= 16; ++o) {
                float p = (float) o * ((float) Math.PI * 2F) / 16.0F;
                float q = Mth.sin(p);
                float r = Mth.cos(p);
                bufferBuilder.addVertex(matrix4f3, q * 120.0F, r * 120.0F, -r * 40.0F * fs[3]).setColor(fs[0], fs[1], fs[2], 0.0F);
            }

            BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
            poseStack.popPose();
        }

        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        poseStack.pushPose();
        float j = 1.0F - info.getRainLevel();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, j);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-90.0F));
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(info.getTimeOfDay() * 360.0F));
        Matrix4f matrix4f4 = poseStack.last().pose();
        float l = 30.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SUN_LOCATION);
        BufferBuilder bufferBuilder2 = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder2.addVertex(matrix4f4, -l, 100.0F, -l).setUv(0.0F, 0.0F);
        bufferBuilder2.addVertex(matrix4f4, l, 100.0F, -l).setUv(1.0F, 0.0F);
        bufferBuilder2.addVertex(matrix4f4, l, 100.0F, l).setUv(1.0F, 1.0F);
        bufferBuilder2.addVertex(matrix4f4, -l, 100.0F, l).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(bufferBuilder2.buildOrThrow());
        l = 20.0F;
        RenderSystem.setShaderTexture(0, MOON_LOCATION);
        int s = info.getMoonPhase();
        int t = s % 4;
        int n = s / 4 % 2;
        float u = (float) (t) / 4.0F;
        float p = (float) (n) / 2.0F;
        float q = (float) (t + 1) / 4.0F;
        float r = (float) (n + 1) / 2.0F;
        bufferBuilder2 = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder2.addVertex(matrix4f4, -l, -100.0F, l).setUv(q, r);
        bufferBuilder2.addVertex(matrix4f4, l, -100.0F, l).setUv(u, r);
        bufferBuilder2.addVertex(matrix4f4, l, -100.0F, -l).setUv(u, p);
        bufferBuilder2.addVertex(matrix4f4, -l, -100.0F, -l).setUv(q, p);
        BufferUploader.drawWithShader(bufferBuilder2.buildOrThrow());
        float v = info.getStarBrightness() * j;
        if (v > 0.0F) {
            RenderSystem.setShaderColor(v, v, v, v);
            FogRenderer.setupNoFog();
            levelRenderer.starBuffer.bind();
            levelRenderer.starBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, GameRenderer.getPositionShader());
            VertexBuffer.unbind();
            skyFogSetup.run();
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        poseStack.popPose();
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);

//        TODO: Determine if this good fit for addon rendering

//        double d = minecraft.player.getEyePosition(partialTick).y - level.getLevelData().getHorizonHeight(level);
//        if (d < (double) 0.0F) {
//            poseStack.pushPose();
//            poseStack.translate(0.0F, 12.0F, 0.0F);
//            levelRenderer.darkBuffer.bind();
//            levelRenderer.darkBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderInstance);
//            VertexBuffer.unbind();
//            poseStack.popPose();
//        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
    }

    public static void renderCloud(OverworldCloudData data, ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        ((CloudRenderBuffer) Minecraft.getInstance().levelRenderer).renderCloudBuffer(poseStack, modelViewMatrix, projectionMatrix, partialTick, ticks, camX, camY, camZ, data.getCloudHeight(), data.getCloudColor());
    }

    public static void renderWeather(OverworldWeatherData data, ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
        ((LevelRendererExtension) Minecraft.getInstance().levelRenderer).renderWeather(lightTexture, partialTick, ticks, camX, camY, camZ, data.getPrecepitation(), data.getRainLevel());
    }
}
