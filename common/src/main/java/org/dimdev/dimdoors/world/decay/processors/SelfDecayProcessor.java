package org.dimdev.dimdoors.world.decay.processors;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.DecayProcessor;

import static org.dimdev.dimdoors.world.decay.DecayProcessor.DecayProcessorType.SELF;

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
        return SELF.get();
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
	public int process(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid) {
        world.setBlockAndUpdate(pos, origin);
        return 0;
    }
}
