package org.dimdev.dimcore.compat.iris.mixins.client;

import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.irisshaders.iris.shadows.ShadowRenderTargets;
import net.irisshaders.iris.shadows.ShadowRenderer;
import org.dimdev.dimcore.compat.iris.node.IrisNodeCompat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ShadowRenderer.class, remap = false)
public abstract class ShadowRendererMixin {
    @Shadow
    @Final
    private ShadowRenderTargets targets;

    @Inject(method = "renderShadows", at = @At("HEAD"))
    private void dimensionalDoors$bindShadowNodes(CallbackInfo ci) {
        IrisNodeCompat compat = IrisNodeCompat.Companion.current();
        if (compat != null) compat.bindShadow(targets);
    }

    @Inject(method = "renderShadows", at = @At("RETURN"))
    private void dimensionalDoors$rebindNodes(CallbackInfo ci) {
        IrisNodeCompat compat = IrisNodeCompat.Companion.current();
        if (compat != null) compat.bind();
    }

    @Inject(method = "renderBlockEntities", at = @At("HEAD"))
    private void dimensionalDoors$collectSodiumBlockEntities(CallbackInfoReturnable<Integer> cir) {
        SodiumWorldRenderer renderer = SodiumWorldRenderer.instanceNullable();
        if (renderer != null && ShadowRenderer.visibleBlockEntities.isEmpty()) renderer.iterateVisibleBlockEntities(ShadowRenderer.visibleBlockEntities::add);
    }
}
