package org.dimdev.dimdoors.world.decay.processors;

import static org.dimdev.dimdoors.world.decay.DecayProcessor.DecayProcessorType.SELF;

import com.google.gson.JsonObject;
import org.dimdev.dimdoors.world.decay.DecayProcessor;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    public int process(World world, BlockPos pos, BlockState origin, BlockState target) {
        world.setBlockState(pos, origin);
        return 0;
    }
}
