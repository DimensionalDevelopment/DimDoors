package org.dimdev.dimdoors.client.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import org.dimdev.dimdoors.client.effect.sky.EnvironmentAddonClient;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;
import org.dimdev.dimdoors.world.pocket.type.addon.sky.EnvironmentAddon;
import org.dimdev.dimdoors.world.pocket.type.addon.sky.SkyData;
import org.joml.Matrix4f;

import java.util.Optional;

public enum DungeonDimensionEffect implements DimensionSpecialEffectsExtensions {
    INSTANCE;

    @Override
    public void renderSky(ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        var data = getEnvironmentAddon(level, camera.getBlockPosition()).map(EnvironmentAddon::getSky).orElse(null);

        processSky(data, level.dimension().equals(ModDimensions.PERSONAL) ? 255 : 0, level, partialTick, modelViewMatrix, camera, projectionMatrix, isFoggy, setupFog);
    }

    @Override
    public void renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        getEnvironmentAddon(level, BlockPos.containing(camX, camY, camZ)).map(EnvironmentAddon::getCloud).ifPresent(data -> EnvironmentAddonClient.renderCloud(data, level, ticks, partialTick, poseStack, camX, camY, camZ, modelViewMatrix, projectionMatrix));
    }

    @Override
    public void renderWeather(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
        getEnvironmentAddon(level, BlockPos.containing(camX, camY, camZ)).map(EnvironmentAddon::getWeather).ifPresent(data -> EnvironmentAddonClient.renderWeather(data, level, ticks, partialTick, lightTexture, camX, camY, camZ));
    }

    private Optional<EnvironmentAddon> getEnvironmentAddon(ClientLevel level, BlockPos pos) {
        return ClientPacketListener.getAddonClient(PocketAddon.PocketAddonType.ENVIRONMENT_ADDON, level, pos);
    }

    private void processSky(SkyData data, int voidColor, ClientLevel level, float partialTick, Matrix4f modelViewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
//        setupFog.run();
        if (!isFoggy) {
            FogType fogtype = camera.getFluidInCamera();
            if (fogtype != FogType.POWDER_SNOW && fogtype != FogType.LAVA && !((LevelRendererExtension) Minecraft.getInstance().levelRenderer).isMobEffectBlockingSky(camera)) {
                PoseStack poseStack = new PoseStack();
                poseStack.mulPose(modelViewMatrix);

                if(data != null) {
                    EnvironmentAddonClient.renderSky(data, level, poseStack, projectionMatrix, partialTick, isFoggy, setupFog, camera);
                } else {
                    RenderSystem.enableBlend();
                    RenderSystem.depthMask(false);

                    LimboDimensionEffect.renderSkyBox(poseStack, voidColor);

                    RenderSystem.depthMask(true);
                    RenderSystem.disableBlend();
                }
            }

        }
    }
}
