package org.dimdev.limlib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.dimdev.limlib.client.IDimensionSpecialEffectExtension;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    @Nullable
    private ClientLevel level;

    @Shadow
    private int ticks;

    @Inject(method = "renderSnowAndRain", at = @At("HEAD"), cancellable = true)
    public void renderWeather(LightTexture lightTexture, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        if(this.level.effects() instanceof IDimensionSpecialEffectExtension extension) {
            var rendered = extension.extRenderWeather(this.level, this.ticks, partialTick, lightTexture, camX, camY, camZ);
            if(rendered) ci.cancel();
        }
    }

    @Inject(method = "tickRain", at = @At("HEAD"), cancellable = true)
    public void tickRain(Camera camera, CallbackInfo ci) {
        if(this.level.effects() instanceof IDimensionSpecialEffectExtension extension) {
            var rendered = extension.extTickRain(this.level, this.ticks, camera);

            if(rendered) ci.cancel();
        }
    }


    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    public void renderSky(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) {
        if(this.level.effects() instanceof IDimensionSpecialEffectExtension extension) {
            var rendered = extension.extRenderSky(this.level, this.ticks, partialTick, frustumMatrix, camera, projectionMatrix, isFoggy, skyFogSetup); ci.cancel();

            if(rendered) ci.cancel();
        }
    }

    @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
    public void renderClouds(PoseStack poseStack, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        if(this.level.effects() instanceof IDimensionSpecialEffectExtension extension) {
            var rendered = extension.extRenderClouds(this.level, this.ticks, partialTick, poseStack, camX, camY, camZ, frustumMatrix, projectionMatrix);

            if(rendered) ci.cancel();
        }
    }
}
