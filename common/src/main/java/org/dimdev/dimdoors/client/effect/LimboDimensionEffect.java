package org.dimdev.dimdoors.client.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.joml.Matrix4f;

public class
LimboDimensionEffect implements DimensionEffect {
    public static final LimboDimensionEffect INSTANCE = new LimboDimensionEffect();
    private static final ResourceLocation MOON_RENDER_PATH = DimensionalDoors.id("textures/other/limbo_moon.png");
    private static final ResourceLocation SUN_RENDER_PATH = DimensionalDoors.id("textures/other/limbo_sun.png");

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        Tesselator tessellator = Tesselator.getInstance();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        RenderSystem.setShaderColor(1, 1, 1, 1);

        PoseStack posestack = new PoseStack();
        posestack.mulPose(modelViewMatrix);

        renderSkyBox(posestack, 40);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);


        float s = 30.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SUN_RENDER_PATH);
        BufferBuilder bufferBuilder  = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(modelViewMatrix, -s, 100.0F, -s).setUv(0.0F, 0.0F);
        bufferBuilder.addVertex(modelViewMatrix, s, 100.0F, -s).setUv(1.0F, 0.0F);
        bufferBuilder.addVertex(modelViewMatrix, s, 100.0F, s).setUv(1.0F, 1.0F);
        bufferBuilder.addVertex(modelViewMatrix, -s, 100.0F, s).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        RenderSystem.setShaderTexture(0, MOON_RENDER_PATH);
        bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(modelViewMatrix, -s, -100.0F, -s).setUv(0.0F, 0.0F);
        bufferBuilder.addVertex(modelViewMatrix, s, -100.0F, -s).setUv(1.0F, 0.0F);
        bufferBuilder.addVertex(modelViewMatrix, s, -100.0F, s).setUv(1.0F, 1.0F);
        bufferBuilder.addVertex(modelViewMatrix, -s, -100.0F, s).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        RenderSystem.depthMask(true);
//        RenderSystem.enableTexture();
        RenderSystem.disableBlend();

        return true;
    }

    public static void renderSkyBox(PoseStack poseStack, int color) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        for (int i = 0; i < 6; ++i) {
            poseStack.pushPose();
            if (i == 1) {
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F));
            }

            if (i == 2) {
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
            }

            if (i == 3) {
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(180.0F));
            }

            if (i == 4) {
                poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F));
            }

            if (i == 5) {
                poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0F));
            }

            Matrix4f matrix4f = poseStack.last().pose();
            var bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            bufferBuilder.addVertex(matrix4f, -100.0F, -100.0F, -100.0F).setUv(0.0F, 0.0F).setColor(color, color, color, 255);
            bufferBuilder.addVertex(matrix4f, -100.0F, -100.0F, 100.0F).setUv(0.0F, 16.0F).setColor(color, color, color, 255);
            bufferBuilder.addVertex(matrix4f, 100.0F, -100.0F, 100.0F).setUv(16.0F, 16.0F).setColor(color, color, color, 255);
            bufferBuilder.addVertex(matrix4f, 100.0F, -100.0F, -100.0F).setUv(16.0F, 0.0F).setColor(color, color, color, 255);
            BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
            poseStack.popPose();
        }


    }
}
