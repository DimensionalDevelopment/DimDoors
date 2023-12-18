package org.dimdev.dimdoors.world.decay.processors;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.DecayProcessor;
import org.dimdev.dimdoors.world.decay.DecayProcessorType;

public class NoneDecayProcessor implements DecayProcessor<Block, ItemStack> {
    public static final String KEY = "none";
    private static final NoneDecayProcessor INSTANCE = new NoneDecayProcessor();

    private NoneDecayProcessor() {}

    public static NoneDecayProcessor instance() {
        return INSTANCE;
    }
    @Override
    public NoneDecayProcessor fromNbt(CompoundTag nbt) {
        return this;
    }

    @Override
    public DecayProcessorType<NoneDecayProcessor> getType() {
        return DecayProcessorType.NONE_PROCESSOR_TYPE.get();
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
