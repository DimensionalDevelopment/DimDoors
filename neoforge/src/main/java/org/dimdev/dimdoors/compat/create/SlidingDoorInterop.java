package org.dimdev.dimdoors.compat.create;

import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.material.Fluids;

public final class SlidingDoorInterop {
    private SlidingDoorInterop() {
    }

    public static BlockPos getOtherDoorPos(BlockPos pos, DoorHingeSide hinge, Direction facing) {
        return pos.relative(hinge == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise());
    }

    public static boolean isCompatibleDoubleDoor(BlockState state, DoorHingeSide hinge, Direction facing, BlockState otherDoor) {
        return hasDoorProperties(state)
                && haveCompatibleBlocks(state, otherDoor)
                && hasDoorProperties(otherDoor)
                && otherDoor.getValue(DoorBlock.HINGE) != hinge
                && otherDoor.getValue(DoorBlock.FACING) == facing
                && otherDoor.getValue(DoorBlock.OPEN) != state.getValue(DoorBlock.OPEN)
                && otherDoor.getValue(DoorBlock.HALF) == state.getValue(DoorBlock.HALF);
    }

    public static boolean isMixedDoubleDoor(BlockState state, DoorHingeSide hinge, Direction facing, BlockState otherDoor) {
        return areCounterparts(state.getBlock(), otherDoor.getBlock())
                && isCompatibleDoubleDoor(state, hinge, facing, otherDoor);
    }

    public static BlockState setOpen(BlockState state, boolean open, boolean hideForAnimation) {
        state = state.setValue(DoorBlock.OPEN, open);
        if ((open || hideForAnimation) && state.hasProperty(SlidingDoorBlock.VISIBLE)) {
            state = state.setValue(SlidingDoorBlock.VISIBLE, false);
        }
        return state;
    }

    public static BlockState setPoweredOpen(BlockState state, boolean powered) {
        state = setOpen(state.setValue(DoorBlock.POWERED, powered), powered, false);
        return state;
    }

    public static void scheduleWaterTickIfNeeded(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide && state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
    }

    private static boolean haveCompatibleBlocks(BlockState state, BlockState otherDoor) {
        Block block = state.getBlock();
        Block otherBlock = otherDoor.getBlock();
        return block == otherBlock || areCounterparts(block, otherBlock);
    }

    private static boolean areCounterparts(Block block, Block otherBlock) {
        if (block instanceof SlidingDimensionalDoorBlock dimensionalDoor && dimensionalDoor.getOriginalBlock() == otherBlock) {
            return true;
        }
        return otherBlock instanceof SlidingDimensionalDoorBlock dimensionalDoor && dimensionalDoor.getOriginalBlock() == block;
    }

    private static boolean hasDoorProperties(BlockState state) {
        return state.hasProperty(DoorBlock.HINGE)
                && state.hasProperty(DoorBlock.FACING)
                && state.hasProperty(DoorBlock.OPEN)
                && state.hasProperty(DoorBlock.HALF);
    }
}
