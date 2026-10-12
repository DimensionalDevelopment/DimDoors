package org.dimdev.dimcore.compat.iris.mixins.client;

import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import org.dimdev.dimcore.client.node.RenderStage;
import org.dimdev.dimcore.client.node.RenderNodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ShadowRenderer.class, remap = false)
public abstract class ShadowRendererMixin {
    @Inject(method = "renderShadows", at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/shadows/ShadowRenderingState;renderBlockEntities", shift = At.Shift.AFTER))
    private void dimensionalDoors$shadowStage(CallbackInfo ci) {
        RenderNodes.render(RenderStage.SHADOW, CapturedRenderingState.INSTANCE.getTickDelta());
    }

    @Inject(method = "renderBlockEntities", at = @At("HEAD"))
    private void dimensionalDoors$collectSodiumBlockEntities(CallbackInfoReturnable<Integer> cir) {
        SodiumWorldRenderer renderer = SodiumWorldRenderer.instanceNullable();
        if (renderer != null && ShadowRenderer.visibleBlockEntities.isEmpty()) renderer.iterateVisibleBlockEntities(ShadowRenderer.visibleBlockEntities::add);
    }
}
