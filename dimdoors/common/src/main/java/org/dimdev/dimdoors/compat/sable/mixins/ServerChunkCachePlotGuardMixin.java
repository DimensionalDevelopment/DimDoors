package org.dimdev.dimdoors.compat.sable.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import org.dimdev.dimdoors.compat.sable.SableLevelSpaceHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerChunkCache.class, priority = 1500)
public class ServerChunkCachePlotGuardMixin {
    @Shadow
    @Final
    private ServerLevel level;

    @Inject(method = "blockChanged", at = @At("HEAD"), cancellable = true)
    private void dimdoors$ignoreMissingSablePlotHolderBlockChange(BlockPos pos, CallbackInfo ci) {
        if (SableLevelSpaceHelper.INSTANCE.isUnavailableNow(this.level, pos)) {
            ci.cancel();
        }
    }
}
