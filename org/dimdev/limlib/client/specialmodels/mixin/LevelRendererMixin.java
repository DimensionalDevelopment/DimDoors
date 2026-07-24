package org.dimdev.limlib.client.specialmodels.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.dimdev.limlib.client.specialmodels.SpecialModelRenderTypes;
import org.dimdev.limlib.client.specialmodels.compat.iris.IrisCompat;
import org.dimdev.limlib.client.specialmodels.compat.sodium.SodiumCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 2000)
public abstract class LevelRendererMixin {

    @Shadow
    private void renderSectionLayer(RenderType renderType, double x, double y, double z, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        throw new AssertionError();
    }

    @Inject(method = "renderSectionLayer", at = @At("HEAD"), cancellable = true)
    private void corners$renderSpecialModelLayers(RenderType renderType, double x, double y, double z, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (IrisCompat.shouldDisableSpecialModelRenderTypes()) {
            if (SpecialModelRenderTypes.isKnownSpecialModelRenderType(renderType)) {
                ci.cancel();
            }

            return;
        }

        if (renderType != RenderType.translucent()) {
            return;
        }

        if (SodiumCompat.isLoaded()) {
            SodiumCompat.renderSpecialModelMeshes(x, y, z, modelViewMatrix, projectionMatrix);
            return;
        }

        for (RenderType specialModelLayer : SpecialModelRenderTypes.chunkBufferLayers()) {
            this.renderSectionLayer(specialModelLayer, x, y, z, modelViewMatrix, projectionMatrix);
        }
    }
}
