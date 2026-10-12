package org.dimdev.dimcore.compat.iris.mixins.client;

import com.google.common.collect.ImmutableList;
import net.irisshaders.iris.shaderpack.include.ShaderPackSourceNames;
import org.dimdev.dimcore.compat.iris.node.IrisNodeCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ShaderPackSourceNames.class, remap = false)
public abstract class ShaderPackSourceNamesMixin {
    @ModifyVariable(method = "findPresentSources", at = @At("HEAD"), argsOnly = true)
    private static ImmutableList<String> dimensionalDoors$addNodeSources(ImmutableList<String> candidates) {
        return ImmutableList.<String>builder().addAll(candidates).addAll(IrisNodeCompat.sourceNames()).build();
    }
}
