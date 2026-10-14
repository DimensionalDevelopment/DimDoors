package org.dimdev.dimcore.mixin.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.dimdev.dimcore.client.node.RenderNodes;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void dimcore$renderLevel(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        RenderNodes.clear();
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;translucent()Lnet/minecraft/client/renderer/RenderType;"))
    private void dimcore$compositeNodes(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (!RenderNodes.INSTANCE.getExternal().invoke()) RenderNodes.composite();
        RenderTarget translucent = ((LevelRenderer) (Object) this).getTranslucentTarget();
        if (translucent != null) translucent.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
    }

    @Inject(method = "resize", at = @At("TAIL"))
    private void dimcore$resize(int width, int height, CallbackInfo ci) {
        RenderNodes.resize(width, height);
    }
}
