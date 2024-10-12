package org.dimdev.dimdoors.client.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.DimensionalDoors;
import org.joml.Matrix4f;

public class LimboDimensionEffect extends DimensionSpecialEffects implements DimensionSpecialEffectsExtensions {
    public static final LimboDimensionEffect INSTANCE = new LimboDimensionEffect();
    private static final ResourceLocation MOON_RENDER_PATH = DimensionalDoors.id("textures/other/limbo_moon.png");
    private static final ResourceLocation SUN_RENDER_PATH = DimensionalDoors.id("textures/other/limbo_sun.png");

    private LimboDimensionEffect() {
        super(-30, true, SkyType.NONE, false, true);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
        return fogColor;
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return false;
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        if(poseStack == null) return true;

        Matrix4f matrix4f = poseStack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        float s = 30.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SUN_RENDER_PATH);
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix4f, -s, 100.0F, -s).setUv(0.0F, 0.0F);
        bufferBuilder.addVertex(matrix4f, s, 100.0F, -s).setUv(1.0F, 0.0F);
        bufferBuilder.addVertex(matrix4f, s, 100.0F, s).setUv(1.0F, 1.0F);
        bufferBuilder.addVertex(matrix4f, -s, 100.0F, s).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        RenderSystem.setShaderTexture(0, MOON_RENDER_PATH);
        bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix4f, -s, -100.0F, -s).setUv(0.0F, 0.0F);
        bufferBuilder.addVertex(matrix4f, s, -100.0F, -s).setUv(1.0F, 0.0F);
        bufferBuilder.addVertex(matrix4f, s, -100.0F, s).setUv(1.0F, 1.0F);
        bufferBuilder.addVertex(matrix4f, -s, -100.0F, s).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        RenderSystem.depthMask(true);
//        RenderSystem.enableTexture();
        RenderSystem.disableBlend();

        return true;
    }

    @Override
    public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
        return true;
    }

    @Override
    public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
        return true;
    }
}
