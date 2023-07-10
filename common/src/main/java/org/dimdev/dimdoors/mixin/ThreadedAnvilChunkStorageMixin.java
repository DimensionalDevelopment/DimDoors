package org.dimdev.dimdoors.mixin;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.dimdev.dimdoors.api.event.ChunkServedCallback;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//100% joinked from Fabric Lifecycle events
@Mixin(ChunkMap.class)
public abstract class ThreadedAnvilChunkStorageMixin {
    @Shadow
    @Final
    ServerLevel level;

    @Inject(method = "method_17227", at = @At("TAIL"))
    private void onChunkLoad(ChunkHolder chunkHolder, ChunkAccess protoChunk, CallbackInfoReturnable<ChunkAccess> callbackInfoReturnable) {
        // We fire the event at TAIL since the chunk is guaranteed to be a WorldChunk then.
        ChunkServedCallback.EVENT.invoker().onChunkServed(this.level, (LevelChunk) callbackInfoReturnable.getReturnValue());
    }
}
