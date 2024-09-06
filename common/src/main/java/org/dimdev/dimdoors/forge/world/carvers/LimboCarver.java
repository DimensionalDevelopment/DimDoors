package org.dimdev.dimdoors.forge.world.carvers;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.fluid.ModFluids;

import java.util.function.Function;

public class LimboCarver
extends CaveWorldCarver {
    public LimboCarver(Codec<CaveCarverConfiguration> codec) {
        super(codec);
        this.liquids = ImmutableSet.of(ModFluids.ETERNAL_FLUID.get());
    }

    @Override
    protected int getCaveBound() {
        return 10;
    }

    @Override
    protected float getThickness(RandomSource random) {
        return (random.nextFloat() * 2.0f + random.nextFloat()) * 2.0f;
    }

    @Override
    protected double getYScale() {
        return 5.0;
    }

    @Override
    protected boolean carveBlock(CarvingContext carvingContext, CaveCarverConfiguration caveCarverConfiguration, ChunkAccess chunkAccess, Function<BlockPos, Holder<Biome>> function, CarvingMask carvingMask, BlockPos.MutableBlockPos mutableBlockPos, BlockPos.MutableBlockPos mutableBlockPos2, Aquifer aquifer, MutableBoolean mutableBoolean) {
        if (this.canReplaceBlock(caveCarverConfiguration, chunkAccess.getBlockState(mutableBlockPos))) {
            BlockState blockState = mutableBlockPos.getY() <= carvingContext.getMinGenY() + 31 ? ModBlocks.ETERNAL_FLUID.get().defaultBlockState() : ModBlocks.LIMBO_AIR.get().defaultBlockState();
            chunkAccess.setBlockState(mutableBlockPos, blockState, false);
            return true;
        }
        return false;
    }
}

