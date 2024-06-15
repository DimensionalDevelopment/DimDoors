package org.dimdev.dimdoors.world.decay.results;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.DecayResult;
import org.dimdev.dimdoors.world.decay.DecayResultType;

import static org.dimdev.dimdoors.world.decay.DecayResultType.SELF;

public class SelfDecayResult implements DecayResult {
    public static final String KEY = "self";

    private static final SelfDecayResult instance = new SelfDecayResult();

    public static SelfDecayResult instance() {
        return instance;
    }

    @Override
    public SelfDecayResult fromNbt(CompoundTag nbt) {
        return this;
    }

    @Override
    public DecayResultType<SelfDecayResult> getType() {
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
