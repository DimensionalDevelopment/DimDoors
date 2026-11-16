package org.dimdev.dimdoors.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.dimdev.dimdoors.decay.Decay;
import org.dimdev.dimdoors.decay.DecaySource;
import org.dimdev.dimdoors.tag.DimensionTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @WrapOperation(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getBlockRandomPos(IIII)Lnet/minecraft/core/BlockPos;"))
    public BlockPos dimdoors$onChunkTickBlockPos(ServerLevel instance, int x, int y, int z, int mask, Operation<BlockPos> original, @Share("pos") LocalRef<BlockPos> pos) {
        var blockPos = original.call(instance, x, y, z, mask);

        if(dimensionalDoors$doesDecay()) pos.set(blockPos);

        return blockPos;
    }

    @Inject(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V", ordinal = 0))
    public void dimdoors$onChunkTick(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci, @Share("pos") LocalRef<BlockPos> pos) {
        var blockPos = pos.get();

        if(blockPos != null) Decay.decayBlock((ServerLevel) (Object) this, DecaySource.LIMBO, pos.get(), null);
        pos.set(null);
    }

    @Unique
    private boolean dimensionalDoors$doesDecay() {
        return ((ServerLevel) (Object) this).dimensionTypeRegistration().is(DimensionTypeTags.UNRAVELLED_FABRIC_CAN_UNRAVEL);
    }
}
