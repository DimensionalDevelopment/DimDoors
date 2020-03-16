package org.dimdev.dimdoors.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class EternalLiquidFluid extends BaseFluid { // TODO
    @Override
    public Fluid getFlowing() {
        return null;
    }

    @Override
    public Fluid getStill() {
        return null;
    }

    @Override
    protected boolean isInfinite() {
        return false;
    }

    @Override
    protected void beforeBreakingBlock(IWorld iWorld, BlockPos blockPos, BlockState blockState) {

    }

    @Override
    protected int method_15733(WorldView worldView) {
        return 0;
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView worldView) {
        return 0;
    }

    @Override
    public Item getBucketItem() {
        return null;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    public int getTickRate(WorldView worldView) {
        return 0;
    }

    @Override
    protected float getBlastResistance() {
        return 0;
    }

    @Override
    protected BlockState toBlockState(FluidState fluidState) {
        return null;
    }

    @Override
    public boolean isStill(FluidState fluidState) {
        return false;
    }

    @Override
    public int getLevel(FluidState fluidState) {
        return 0;
    }
}
