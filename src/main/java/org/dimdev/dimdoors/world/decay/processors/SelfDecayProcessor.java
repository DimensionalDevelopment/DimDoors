package org.dimdev.dimdoors.world.decay.processors;

import org.dimdev.dimdoors.world.decay.DecayProcessor;

import static org.dimdev.dimdoors.world.decay.DecayProcessor.DecayProcessorType.SELF;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SelfDecayProcessor implements DecayProcessor {
    public static final String KEY = "self";

    private static final SelfDecayProcessor instance = new SelfDecayProcessor();

    public static SelfDecayProcessor instance() {
        return instance;
    }

    @Override
    public DecayProcessor fromNbt(CompoundTag nbt) {
        return this;
    }

    @Override
    public DecayProcessorType<? extends DecayProcessor> getType() {
        return SELF;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public int process(Level world, BlockPos pos, BlockState origin, BlockState target) {
        world.setBlockAndUpdate(pos, origin);
        return 0;
    }
}
