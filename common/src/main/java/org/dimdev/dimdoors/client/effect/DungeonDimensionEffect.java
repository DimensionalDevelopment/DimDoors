package org.dimdev.dimdoors.client.effect;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FogType;
import org.dimdev.dimdoors.client.effect.sky.EnvironmentAddonClient;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;
import org.dimdev.dimdoors.world.pocket.type.addon.sky.EnvironmentAddon;
import org.dimdev.dimdoors.world.pocket.type.addon.sky.SkyData;
import org.joml.Matrix4f;

import java.util.Optional;

public class DungeonDimensionEffect implements DimensionSpecialEffectsExtensions {
    public static DungeonDimensionEffect INSTANCE = new DungeonDimensionEffect();

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        return getEnvironmentAddon(level, camera.getBlockPosition()).map(EnvironmentAddon::getSky).map(data -> processSky(data, level, partialTick, modelViewMatrix, camera, projectionMatrix, isFoggy, setupFog)).orElse(false);
    }



    @Override
    public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        return getEnvironmentAddon(level, BlockPos.containing(camX, camY, camZ)).map(EnvironmentAddon::getCloud).map(data -> {
            EnvironmentAddonClient.renderCloud(data, level, ticks, partialTick, poseStack, camX, camY, camZ, modelViewMatrix, projectionMatrix);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean renderWeather(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
        return getEnvironmentAddon(level, BlockPos.containing(camX, camY, camZ)).map(EnvironmentAddon::getWeather).map(data -> {
            EnvironmentAddonClient.renderWeather(data, level, ticks, partialTick, lightTexture, camX, camY, camZ);
            return true;
        }).orElse(false);
    }

    private Optional<EnvironmentAddon> getEnvironmentAddon(ClientLevel level, BlockPos pos) {
        return ClientPacketListener.getAddonClient(PocketAddon.PocketAddonType.ENVIRONMENT_ADDON.get(), level, pos);
    }

    private boolean processSky(SkyData data, ClientLevel level, float partialTick, Matrix4f modelViewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
//        setupFog.run();
        if (!isFoggy) {
            FogType fogtype = camera.getFluidInCamera();
            if (fogtype != FogType.POWDER_SNOW && fogtype != FogType.LAVA && !((LevelRendererExtension) Minecraft.getInstance().levelRenderer).isMobEffectBlockingSky(camera)) {
                PoseStack poseStack = new PoseStack();
                poseStack.mulPose(modelViewMatrix);

                EnvironmentAddonClient.renderSky(data, level, poseStack, projectionMatrix, partialTick, isFoggy, setupFog, camera);
            }

        }

        return true;
    }
}
