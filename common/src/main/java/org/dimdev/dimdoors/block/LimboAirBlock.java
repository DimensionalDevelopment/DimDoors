package org.dimdev.dimdoors.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.forge.world.ModDimensions;
import org.dimdev.dimdoors.forge.world.decay.Decay;
import org.dimdev.dimdoors.forge.world.decay.DecaySource;

public class LimboAirBlock extends AirBlock {
    public LimboAirBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (ModDimensions.isLimboDimension(level)) {
            Decay.applySpreadDecay(level, pos, random, DecaySource.LIMBO);
        }
    }
}
