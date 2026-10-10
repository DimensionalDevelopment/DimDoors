package org.dimdev.dimdoors.mixin.client.iris;

import net.irisshaders.iris.uniforms.CapturedRenderingState;
import org.dimdev.dimdoors.client.render.node.NodeStage;
import org.dimdev.dimdoors.client.render.node.RenderNodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.irisshaders.iris.shadows.ShadowRenderer", remap = false)
public abstract class ShadowRendererMixin {
    @Inject(method = "renderShadows", at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/shadows/ShadowRenderingState;renderBlockEntities", shift = At.Shift.AFTER))
    private void dimensionalDoors$shadowStage(CallbackInfo ci) {
        RenderNodes.render(NodeStage.SHADOW, CapturedRenderingState.INSTANCE.getTickDelta());
    }
}
