package org.dimdev.dimdoors.client.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.listener.pocket.PocketListenerUtil;
import org.dimdev.dimdoors.mixin.client.accessor.DimensionSpecialEffectsMixin;
import org.dimdev.dimdoors.world.pocket.type.addon.SkyAddon;
import org.joml.Matrix4f;

import java.util.List;

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
            } else if (DimensionSpecialEffectsMixin.getEffects().containsKey(key)) {
                var effects = DimensionSpecialEffectsMixin.getEffects().get(key);

                if (effects != null) {
                    renderEffect(effects, level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
                }
            }
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
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, END_SKY_LOCATION);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();

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
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).uv(0.0F, 0.0F).color(40, 40, 40, 255).endVertex();
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).uv(0.0F, 16.0F).color(40, 40, 40, 255).endVertex();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).uv(16.0F, 16.0F).color(40, 40, 40, 255).endVertex();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).uv(16.0F, 0.0F).color(40, 40, 40, 255).endVertex();
            tesselator.end();
            poseStack.popPose();
        }

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
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
