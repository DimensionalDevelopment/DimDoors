package org.dimdev.dimdoors.world.decay.processors;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.dimdev.dimdoors.world.decay.DecayProcessor;

import static org.dimdev.dimdoors.world.decay.DecayProcessor.DecayProcessorType.SELF;

public class SelfDecayProcessor implements DecayProcessor {
    public static final String KEY = "self";

    private static final SelfDecayProcessor instance = new SelfDecayProcessor();

    public static SelfDecayProcessor instance() {
        return instance;
    }

    @Override
    public DecayProcessor fromNbt(NbtCompound nbt) {
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
	public int process(World world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid) {
        world.setBlockState(pos, origin);
        return 0;
    }
}
