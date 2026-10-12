package org.dimdev.dimcore.compat.iris.mixins.client;

import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.targets.RenderTargets;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import org.dimdev.dimcore.client.node.RenderStage;
import org.dimdev.dimcore.client.node.RenderNodes;
import org.dimdev.dimcore.compat.iris.node.IrisNodeCompat;
import org.dimdev.dimcore.compat.iris.node.IrisRenderingPipelineExt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = IrisRenderingPipeline.class, remap = false)
public abstract class IrisRenderingPipelineMixin implements IrisRenderingPipelineExt {
    @Shadow
    @Final
    private RenderTargets renderTargets;

    @Unique
    private IrisNodeCompat dimensionalDoors$nodeCompat;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void dimensionalDoors$createNodeCompat(ProgramSet programSet, CallbackInfo ci) {
        dimensionalDoors$nodeCompat = new IrisNodeCompat((IrisRenderingPipeline) (Object) this, programSet);
    }

    @Inject(method = "beginTranslucents", at = @At("HEAD"))
    private void dimensionalDoors$gbufferStage(CallbackInfo ci) {
        RenderNodes.render(RenderStage.OPAQUE, CapturedRenderingState.INSTANCE.getTickDelta());
    }

    @Inject(method = "beginTranslucents", at = @At("RETURN"))
    private void dimensionalDoors$translucentStage(CallbackInfo ci) {
        RenderNodes.render(RenderStage.TRANSLUCENT, CapturedRenderingState.INSTANCE.getTickDelta());
        renderTargets.copyPreTranslucentDepth();
    }

    @Inject(method = "destroy", at = @At("HEAD"))
    private void dimensionalDoors$clearNodeCompat(CallbackInfo ci) {
        dimensionalDoors$nodeCompat.clear();
    }

    @Override
    public IrisNodeCompat dimensionalDoors$getNodeCompat() {
        return dimensionalDoors$nodeCompat;
    }

    @Override
    public RenderTargets dimensionalDoors$getRenderTargets() {
        return renderTargets;
    }
}
