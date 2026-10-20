package org.dimdev.dimcore.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.dimdev.dimcore.client.DebugRenders;
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

    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/debug/DebugRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V"))
    private void dimcore$debugRender(DebugRenderer debugRenderer, PoseStack poseStack, MultiBufferSource.BufferSource buffers, double camX, double camY, double camZ, Operation<Void> original) {
        original.call(debugRenderer, poseStack, buffers, camX, camY, camZ);
        poseStack.pushPose();
        poseStack.translate(-camX, -camY, -camZ);
        DebugRenders.render(poseStack, buffers);
        poseStack.popPose();
    }

    @Inject(method = "resize", at = @At("TAIL"))
    private void dimcore$resize(int width, int height, CallbackInfo ci) {
        RenderNodes.resize(width, height);
    }
}
