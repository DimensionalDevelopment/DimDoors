package org.dimdev.dimcore.compat.iris.mixins.client;

import net.irisshaders.iris.Iris;
import org.dimdev.dimcore.compat.iris.node.IrisNodeCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Iris.class, remap = false)
public abstract class IrisMixin {
    @Inject(method = "onLoadingComplete", at = @At("HEAD"))
    private static void dimensionalDoors$rescanNodes(CallbackInfo ci) {
        IrisNodeCompat.reloadIfStale();
    }
}
