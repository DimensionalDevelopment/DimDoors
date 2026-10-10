package org.dimdev.dimdoors.mixin.client.iris;

import com.google.common.collect.ImmutableList;
import org.dimdev.dimdoors.client.render.node.iris.IrisNodeCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "net.irisshaders.iris.shaderpack.include.ShaderPackSourceNames", remap = false)
public abstract class ShaderPackSourceNamesMixin {
    @ModifyVariable(method = "findPresentSources", at = @At("HEAD"), argsOnly = true)
    private static ImmutableList<String> dimensionalDoors$addNodeSources(ImmutableList<String> candidates) {
        return ImmutableList.<String>builder().addAll(candidates).addAll(IrisNodeCompat.sourceNames()).build();
    }
}
