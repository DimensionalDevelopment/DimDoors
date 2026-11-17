package org.dimdev.dimdoors.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.dimdev.dimdoors.decay.Decay;
import org.dimdev.dimdoors.decay.DecaySource;
import org.dimdev.dimdoors.tag.DimensionTypeTags;
import org.dimdev.dimdoors.util.ServerLevelExt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends LevelMixin implements ServerLevelExt {
    @Inject(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", ordinal = 1))
    public void dimdoors$onChunkTick(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci, @Local ProfilerFiller profilerfiller, @Local(ordinal = 1) int i, @Local(ordinal = 2) int j) {
        if (randomTickSpeed > 0 && dimensionalDoors$doesDecay()) {
            LevelChunkSection[] alevelchunksection = chunk.getSections();

            for(int j1 = 0; j1 < alevelchunksection.length; ++j1) {
                LevelChunkSection levelchunksection = alevelchunksection[j1];
                if (!levelchunksection.hasOnlyAir()) {
                    int k1 = chunk.getSectionYFromSectionIndex(j1);
                    int k = SectionPos.sectionToBlockCoord(k1);

                    for(int l = 0; l < randomTickSpeed; ++l) {
                        BlockPos blockpos1 = this.getBlockRandomPos(i, k, j, 15);
                        profilerfiller.push("decayTick");

                        Decay.decayBlock((ServerLevel) (Object) this, DecaySource.LIMBO, blockpos1);

                        profilerfiller.pop();
                    }
                }
            }
        }
    }

    @Unique
    private boolean dimensionalDoors$doesDecay() {
        return ((ServerLevel) (Object) this).dimensionTypeRegistration().is(DimensionTypeTags.ALLOWS_DECAY);
    }
}
