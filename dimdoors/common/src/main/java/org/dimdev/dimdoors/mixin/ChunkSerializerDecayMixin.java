package org.dimdev.dimdoors.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.ticks.LevelChunkTicks;
import org.dimdev.dimdoors.decay.Decay;
import org.dimdev.dimdoors.util.LevelChunkExt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerDecayMixin {
    @Unique private static final String DECAY_TICKS_TAG = "dimdoors:decay_ticks";

    @ModifyExpressionValue(method = "read", at = @At(value = "NEW", target = "net/minecraft/world/level/chunk/LevelChunk"))
    private static LevelChunk dimdoors$loadDecayTicks(LevelChunk chunk, @Local(argsOnly = true) ServerLevel level, @Local(argsOnly = true) ChunkPos pos, @Local(argsOnly = true) CompoundTag tag) {
        ((LevelChunkExt) chunk).dimensionalDoors$setDecayTicks(LevelChunkTicks.load(tag.getList(DECAY_TICKS_TAG, Tag.TAG_COMPOUND), id -> Decay.DecayTask.decode(level, id), pos));
        return chunk;
    }

    @ModifyReturnValue(method = "write", at = @At("RETURN"))
    private static CompoundTag dimdoors$saveDecayTicks(CompoundTag tag, @Local(argsOnly = true) ServerLevel level, @Local(argsOnly = true) ChunkAccess chunk) {
        if (chunk instanceof ImposterProtoChunk imposter) chunk = imposter.getWrapped();
        if (chunk instanceof LevelChunkExt ext) tag.put(DECAY_TICKS_TAG, ext.dimensionalDoors$getDecayTicks().save(level.getLevelData().getGameTime(), Decay.DecayTask::encode));
        return tag;
    }
}
