package org.dimdev.dimcore.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimcore.client.node.RenderNodes;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "renderLevel", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args = "ldc=destroyProgress"))
    private void dimcore$renderLevel(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        Vec3 pos = camera.getPosition();
        RenderNodes.renderLevel(deltaTracker.getGameTimeDeltaTicks(), () -> ((LevelRendererAccessor) this).dimcore$renderSectionLayer(RenderType.translucent(), pos.x, pos.y, pos.z, frustumMatrix, projectionMatrix));
    }

    @Inject(method = "resize", at = @At("TAIL"))
    private void dimcore$resize(int width, int height, CallbackInfo ci) {
        RenderNodes.resize(width, height);
    }
}
