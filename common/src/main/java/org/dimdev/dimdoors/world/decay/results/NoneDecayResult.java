package org.dimdev.dimdoors.world.decay.results;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.DecayResult;
import org.dimdev.dimdoors.world.decay.DecayResultType;

public class NoneDecayResult implements DecayResult {
    public static final String KEY = "none";
    private static final NoneDecayResult INSTANCE = new NoneDecayResult();

    private NoneDecayResult() {}

    public static NoneDecayResult instance() {
        return INSTANCE;
    }
    @Override
    public NoneDecayResult fromNbt(CompoundTag nbt) {
        return this;
    }

    @Override
    public DecayResultType<NoneDecayResult> getType() {
        return DecayResultType.NONE_PROCESSOR_TYPE.get();
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public int process(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid) {
        return 0;
    }

    @Override
    public Object produces(Object prior) {
        return ItemStack.EMPTY;
    }
}
