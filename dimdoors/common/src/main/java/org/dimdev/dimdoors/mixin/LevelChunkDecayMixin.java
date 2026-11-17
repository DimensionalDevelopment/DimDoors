package org.dimdev.dimdoors.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.ticks.LevelChunkTicks;
import org.dimdev.dimdoors.decay.Decay;
import org.dimdev.dimdoors.util.LevelChunkExt;
import org.dimdev.dimdoors.util.ServerLevelExt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public class LevelChunkDecayMixin implements LevelChunkExt {
    @Unique LevelChunkTicks<Decay.DecayTask> dimensionalDoors$decayTaskLevelTicks = new LevelChunkTicks<>();

    @Override
    public LevelChunkTicks<Decay.DecayTask> dimensionalDoors$getDecayTicks() {
        return dimensionalDoors$decayTaskLevelTicks;
    }

    @Override
    public void dimensionalDoors$setDecayTicks(LevelChunkTicks<Decay.DecayTask> ticks) {
        dimensionalDoors$decayTaskLevelTicks = ticks;
    }

    @Inject(method = "unpackTicks", at = @At("TAIL"))
    private void dimdoors$unpackDecayTicks(long gameTime, CallbackInfo ci) {
        dimensionalDoors$decayTaskLevelTicks.unpack(gameTime);
    }

    @Inject(method = "registerTickContainerInLevel", at = @At("TAIL"))
    private void dimdoors$registerDecayTicks(ServerLevel level, CallbackInfo ci) {
        ((ServerLevelExt) level).dimensionalDoors$getDecayTicks().addContainer(((LevelChunk) (Object) this).getPos(), dimensionalDoors$decayTaskLevelTicks);
    }

    @Inject(method = "unregisterTickContainerFromLevel", at = @At("TAIL"))
    private void dimdoors$unregisterDecayTicks(ServerLevel level, CallbackInfo ci) {
        ((ServerLevelExt) level).dimensionalDoors$getDecayTicks().removeContainer(((LevelChunk) (Object) this).getPos());
    }
}
