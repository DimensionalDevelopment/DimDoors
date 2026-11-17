package org.dimdev.dimdoors.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.ticks.LevelTicks;
import org.dimdev.dimdoors.decay.Decay;
import org.dimdev.dimdoors.util.ServerLevelExt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelDecayMixin extends LevelMixin implements ServerLevelExt {
    @Shadow
    protected abstract boolean isPositionTickingWithEntitiesLoaded(long chunkPos);

    @Unique private LevelTicks<Decay.DecayTask> dimensionalDoors$decayTaskTicks;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void dimdoors$init(MinecraftServer server, Executor dispatcher, LevelStorageSource.LevelStorageAccess levelStorageAccess, ServerLevelData serverLevelData, ResourceKey dimension, LevelStem levelStem, ChunkProgressListener progressListener, boolean isDebug, long biomeZoomSeed, List customSpawners, boolean tickTime, RandomSequences randomSequences, CallbackInfo ci) {
        dimensionalDoors$decayTaskTicks = new LevelTicks<>(this::isPositionTickingWithEntitiesLoaded, this.getProfilerSupplier());
    }

    @Override
    public LevelTicks<Decay.DecayTask> dimensionalDoors$getDecayTicks() {
        return dimensionalDoors$decayTaskTicks;
    }

    @Inject(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V", ordinal = 0))
    public void dimdoors$tick(BooleanSupplier hasTimeLeft, CallbackInfo ci, @Local long k) {
        dimensionalDoors$decayTaskTicks.tick(k, 65536, this::dimensionalDoors$tickDecayTask);
    }

    @Unique private void dimensionalDoors$tickDecayTask(BlockPos blockPos, Decay.DecayTask decayTask) {
        decayTask.process((ServerLevel) (Object) this, blockPos);
    }
}
