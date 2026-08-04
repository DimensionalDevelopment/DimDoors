package org.dimdev.dimdoors.mixin;

import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.world.ModDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {
    @Unique
    private static final int DIMDOORS$SECTION_WIDTH = 16;

    @Inject(method = "applyBiomeDecoration", at = @At("TAIL"))
    private void dimdoors$replaceGeneratedLimboAir(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci) {
        if ((Object) this instanceof NoiseBasedChunkGenerator generator && generator.stable(ModDimensions.LIMBO_NOISE_SETTINGS)) {
            dimdoors$replaceGeneratedAir(chunk);
        }
    }

    @Unique
    private static void dimdoors$replaceGeneratedAir(ChunkAccess chunk) {
        BlockState limboAir = ModBlocks.LIMBO_AIR.defaultBlockState();
        boolean changed = false;

        for (LevelChunkSection section : chunk.getSections()) {
            changed |= dimdoors$replaceGeneratedAir(section, limboAir);
        }

        if (changed) {
            chunk.setUnsaved(true);
        }
    }

    @Unique
    private static boolean dimdoors$replaceGeneratedAir(LevelChunkSection section, BlockState limboAir) {
        boolean changed = false;
        section.acquire();
        try {
            for (int x = 0; x < DIMDOORS$SECTION_WIDTH; x++) {
                for (int y = 0; y < DIMDOORS$SECTION_WIDTH; y++) {
                    for (int z = 0; z < DIMDOORS$SECTION_WIDTH; z++) {
                        if (section.getBlockState(x, y, z).isAir()) {
                            section.setBlockState(x, y, z, limboAir, false);
                            changed = true;
                        }
                    }
                }
            }
        } finally {
            section.release();
        }

        return changed;
    }
}
