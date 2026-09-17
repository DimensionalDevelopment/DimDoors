package org.dimdev.dimcore.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;

public interface IDimensionSpecialEffectExtension {
    default boolean extRenderWeather(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
        return false;
    }

    default boolean extTickRain(ClientLevel level, int ticks, Camera camera) {
        return false;
    }

    default boolean extRenderSky(ClientLevel level, int ticks, float partialTick, Matrix4f frustumMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable skyFogSetup) {
        return false;
    }

    default boolean extRenderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        return false;
    }
}
