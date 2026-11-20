package org.dimdev.dimcore.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.dimdev.dimcore.client.node.RenderNodes;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "renderItemInHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LightTexture;turnOffLightLayer()V"))
    private void dimcore$compositeHand(Camera camera, float partialTick, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (!RenderNodes.INSTANCE.getExternal().invoke()) RenderNodes.composite();
        RenderNodes.clear();
    }
}
