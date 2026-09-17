package org.dimdev.dimcore.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.dimdev.dimcore.client.IDimensionSpecialEffectExtension;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRenderDimensionalEffectsMixin {
    @Shadow
    @Nullable
    private ClientLevel level;

    @Shadow
    private int ticks;

    @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
    public void renderClouds(PoseStack poseStack, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        if(level.effects() instanceof IDimensionSpecialEffectExtension effects) {
            if(effects.extRenderClouds(level, ticks, partialTick, poseStack, camX, camY, camZ, frustumMatrix, projectionMatrix)) ci.cancel();
        }
    }

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    public void renderSky(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) {
        if(level.effects() instanceof IDimensionSpecialEffectExtension effects) {
            if(effects.extRenderSky(level, ticks, partialTick, frustumMatrix, camera, projectionMatrix, isFoggy, skyFogSetup)) ci.cancel();
        }
    }

    @Inject(method = "renderSnowAndRain", at = @At("HEAD"), cancellable = true)
    public void renderWeather(LightTexture lightTexture, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        if(level.effects() instanceof IDimensionSpecialEffectExtension effects) {
            if(effects.extRenderWeather(level, ticks, partialTick, lightTexture, camX, camY, camZ)) ci.cancel();
        }
    }

    @Inject(method = "tickRain", at = @At("HEAD"), cancellable = true)
    public void renderClouds(Camera camera, CallbackInfo ci) {
        if(level.effects() instanceof IDimensionSpecialEffectExtension effects) {
            if(effects.extTickRain(level, ticks, camera)) ci.cancel();
        }
    }
}
