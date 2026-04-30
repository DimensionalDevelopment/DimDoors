package org.dimdev.dimdoors.mixin;

import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;

//100% joinked from Fabric Lifecycle events
@Mixin(ChunkMap.class)
public abstract class ThreadedAnvilChunkStorageMixin {
//    @Shadow
//    @Final
//    ServerLevel level;
//
//    @Inject(method = "method_17227", at = @At("TAIL"))
//    private void onChunkLoad(ChunkHolder chunkHolder, ChunkAccess protoChunk, CallbackInfoReturnable<ChunkAccess> callbackInfoReturnable) {
//        // We fire the event at TAIL since the chunk is guaranteed to be a WorldChunk then.
//        ChunkServedCallback.EVENT.invoker().onChunkServed(this.level, (LevelChunk) callbackInfoReturnable.getReturnValue());
//    }
}
