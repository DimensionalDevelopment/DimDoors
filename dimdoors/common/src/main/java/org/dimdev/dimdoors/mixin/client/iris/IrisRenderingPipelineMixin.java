package org.dimdev.dimdoors.mixin.client.iris;

import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.targets.RenderTargets;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import org.dimdev.dimdoors.client.render.node.NodeStage;
import org.dimdev.dimdoors.client.render.node.RenderNodes;
import org.dimdev.dimdoors.client.render.node.iris.IrisNodeCompat;
import org.dimdev.dimdoors.client.render.node.iris.IrisRenderingPipelineExt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.irisshaders.iris.pipeline.IrisRenderingPipeline", remap = false)
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
        RenderNodes.render(NodeStage.GBUFFER, CapturedRenderingState.INSTANCE.getTickDelta());
    }

    @Inject(method = "beginTranslucents", at = @At("RETURN"))
    private void dimensionalDoors$translucentStage(CallbackInfo ci) {
        RenderNodes.render(NodeStage.TRANSLUCENT, CapturedRenderingState.INSTANCE.getTickDelta());
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
