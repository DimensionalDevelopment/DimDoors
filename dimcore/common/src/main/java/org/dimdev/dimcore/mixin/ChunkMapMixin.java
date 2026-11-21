package org.dimdev.dimcore.mixin;

import com.mojang.datafixers.DataFixer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import org.dimdev.dimcore.api.event.ChunkEvents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(ChunkMap.class)
public class ChunkMapMixin extends ChunkStorage {
    @Shadow
    @Final
    ServerLevel level;

    public ChunkMapMixin(RegionStorageInfo info, Path folder, DataFixer fixerUpper, boolean sync) {
        super(info, folder, fixerUpper, sync);
    }

    @Inject(method = "save(Lnet/minecraft/world/level/chunk/ChunkAccess;)Z", at = @At("HEAD"), cancellable = true)
    private void dimcore$allowChunkSave(ChunkAccess chunk, CallbackInfoReturnable<Boolean> cir) {
        if (!ChunkEvents.ALLOW.invoker().invoke(level, chunk)) {
            cir.setReturnValue(false);
        }
    }
}