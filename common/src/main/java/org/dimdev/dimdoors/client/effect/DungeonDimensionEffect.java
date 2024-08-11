package org.dimdev.dimdoors.client.effect;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.listener.pocket.PocketListenerUtil;
import org.dimdev.dimdoors.world.pocket.type.addon.SkyAddon;
import org.joml.Matrix4f;

import java.util.List;

import static net.minecraft.client.renderer.LevelRenderer.MOON_LOCATION;
import static net.minecraft.client.renderer.LevelRenderer.SUN_LOCATION;
import static net.minecraft.client.renderer.blockentity.TheEndPortalRenderer.END_SKY_LOCATION;

public class DungeonDimensionEffect extends DimensionSpecialEffects implements DimensionSpecialEffectsExtensions {
    public static DungeonDimensionEffect INSTANCE = new DungeonDimensionEffect();
    private DungeonDimensionEffect() {
        super(-30, false, SkyType.NONE, false, true);
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
        ClientLevel world = level;
        List<SkyAddon> skyAddons = PocketListenerUtil.applicableAddonsClient(SkyAddon.class, world, camera.getBlockPosition());
        SkyAddon skyAddon = null;
        if (skyAddons.size() > 0) {
            // There should really only be one of these.
            // If anyone needs to use multiple SkyAddons then go ahead and change this.
            skyAddon = skyAddons.get(0);
        }

        if (skyAddon != null) {
            ResourceLocation key = skyAddon.getEffect();

            if (key.equals(BuiltinDimensionTypes.END_EFFECTS)) {
                renderEndSky(poseStack);
            } else if(key.equals(BuiltinDimensionTypes.OVERWORLD_EFFECTS)) {
                renderOverworld(skyAddon, poseStack, projectionMatrix, partialTick, isFoggy, setupFog);
            } /*else if (DimensionSpecialEffectsMixin.getEffects().containsKey(key)) {
                var effects = DimensionSpecialEffectsMixin.getEffects().get(key);

                if (effects != null) {
                    renderEffect(effects, level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
                }
            }*/
        }

        return true;
    }

    @ExpectPlatform
    public static void renderEffect(DimensionSpecialEffects effect, ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        throw new RuntimeException();
    }

    private void renderEndSky(PoseStack poseStack) {
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);

        for(int i = 0; i < 6; ++i) {
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

            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, END_SKY_LOCATION);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

            bufferBuilder.addVertex(matrix4f, -100.0F, -100.0F, -100.0F).setUv(0.0F, 0.0F).setColor(40, 40, 40, 255)/*.endVertex()*/;
            bufferBuilder.addVertex(matrix4f, -100.0F, -100.0F, 100.0F).setUv(0.0F, 16.0F).setColor(40, 40, 40, 255)/*.endVertex()*/;
            bufferBuilder.addVertex(matrix4f, 100.0F, -100.0F, 100.0F).setUv(16.0F, 16.0F).setColor(40, 40, 40, 255)/*.endVertex()*/;
            bufferBuilder.addVertex(matrix4f, 100.0F, -100.0F, -100.0F).setUv(16.0F, 0.0F).setColor(40, 40, 40, 255)/*.endVertex()*/;
//            tesselator.end();
            poseStack.popPose();
        }

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    public void renderOverworld(SkyAddon info, PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, boolean isFoggy, Runnable skyFogSetup) {
        var renderer = Minecraft.getInstance().levelRenderer;

        Vec3 vec3 = info.getSkyColor(); //this.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getPosition(), partialTick);
        float f = (float)vec3.x;
        float g = (float)vec3.y;
        float h = (float)vec3.z;
        FogRenderer.levelFogColor();
        Tesselator tesselator = Tesselator.getInstance();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(f, g, h, 1.0F);
        ShaderInstance shaderInstance = RenderSystem.getShader();
        renderer.skyBuffer.bind();
        renderer.skyBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderInstance);
        VertexBuffer.unbind();
        RenderSystem.enableBlend();
        float[] fs = Minecraft.getInstance().level.effects().getSunriseColor(info.getTimeOfDay(), partialTick);
        float i;
        float k;
        float o;
        float p;
        float q;

        if (fs != null) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.pushPose();
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F));
            i = Mth.sin(info.getSunAngle()) < 0.0F ? 180.0F : 0.0F;
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(i));
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F));
            float j = fs[0];
            k = fs[1];
            float l = fs[2];
            Matrix4f matrix4f = poseStack.last().pose();
            BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
            bufferBuilder.addVertex(matrix4f, 0.0F, 100.0F, 0.0F).setColor(j, k, l, fs[3]);

            for(int n = 0; n <= 16; ++n) {
                o = (float)n * 6.2831855F / 16.0F;
                p = Mth.sin(o);
                q = Mth.cos(o);
                bufferBuilder.addVertex(matrix4f, p * 120.0F, q * 120.0F, -q * 40.0F * fs[3]).setColor(fs[0], fs[1], fs[2], 0.0F);
            }

            BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
            poseStack.popPose();
        }

        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        poseStack.pushPose();
        i = 1.0F - info.getRainLevel();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, i);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-90.0F));
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(info.getTimeOfDay() * 360.0F));
        Matrix4f matrix4f2 = poseStack.last().pose();
        k = 30.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SUN_LOCATION);
        BufferBuilder bufferBuilder2 = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder2.addVertex(matrix4f2, -k, 100.0F, -k).setUv(0.0F, 0.0F);
        bufferBuilder2.addVertex(matrix4f2, k, 100.0F, -k).setUv(1.0F, 0.0F);
        bufferBuilder2.addVertex(matrix4f2, k, 100.0F, k).setUv(1.0F, 1.0F);
        bufferBuilder2.addVertex(matrix4f2, -k, 100.0F, k).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(bufferBuilder2.buildOrThrow());
        k = 20.0F;
        RenderSystem.setShaderTexture(0, MOON_LOCATION);
        int r = info.getMoonPhase();
        int s = r % 4;
        int m = r / 4 % 2;
        float t = (float)(s + 0) / 4.0F;
        o = (float)(m + 0) / 2.0F;
        p = (float)(s + 1) / 4.0F;
        q = (float)(m + 1) / 2.0F;
        bufferBuilder2 = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder2.addVertex(matrix4f2, -k, -100.0F, k).setUv(p, q);
        bufferBuilder2.addVertex(matrix4f2, k, -100.0F, k).setUv(t, q);
        bufferBuilder2.addVertex(matrix4f2, k, -100.0F, -k).setUv(t, o);
        bufferBuilder2.addVertex(matrix4f2, -k, -100.0F, -k).setUv(p, o);
        BufferUploader.drawWithShader(bufferBuilder2.buildOrThrow());
        float u = info.getStarBrightness() * i;
        if (u > 0.0F) {
            RenderSystem.setShaderColor(u, u, u, u);
            FogRenderer.setupNoFog();
            renderer.starBuffer.bind();
            renderer.starBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, GameRenderer.getPositionShader());
            VertexBuffer.unbind();
            skyFogSetup.run();
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        poseStack.popPose();
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
        double d = Minecraft.getInstance().player.getEyePosition(partialTick).y - info.getHorizonHeight();
        if (d < 0.0) {
            poseStack.pushPose();
            poseStack.translate(0.0F, 12.0F, 0.0F);
            renderer.darkBuffer.bind();
            renderer.darkBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderInstance);
            VertexBuffer.unbind();
            poseStack.popPose();
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
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
