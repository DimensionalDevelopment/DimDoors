package org.dimdev.dimdoors.compat.create;

import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.dimdev.dimdoors.block.DimensionalPortalBlock;
import org.dimdev.dimdoors.block.DoorSoundProvider;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.jetbrains.annotations.Nullable;

public class SlidingDimensionalDoorBlock extends DimensionalDoorBlockRegistrar.AutoGenDimensionalDoorBlock {

    public SlidingDimensionalDoorBlock(Properties settings, DoorSoundProvider originalBlock) {
        super(settings, originalBlock);
    }

    public boolean isFoldingDoor() {
        return ((SlidingDoorBlock) originalBlock).isFoldingDoor();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SlidingDoorBlock.VISIBLE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!state.getValue(OPEN) && state.getValue(SlidingDoorBlock.VISIBLE)) {
            return super.getShape(state, level, pos, context);
        }

        Direction direction = state.getValue(FACING);
        boolean hinge = state.getValue(HINGE) == DoorHingeSide.RIGHT;
        return SlidingDoorShapes.get(direction, hinge, isFoldingDoor());
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getShape(state, level, pos, CollisionContext.empty());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateForPlacement = super.getStateForPlacement(context);
        if (stateForPlacement != null && stateForPlacement.getValue(OPEN)) {
            return stateForPlacement.setValue(OPEN, false)
                    .setValue(POWERED, false);
        }
        return stateForPlacement;
    }



    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(this)) {
            deferUpdate(level, pos);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        BlockState updatedState = super.updateShape(state, direction, neighborState, level, currentPos, facingPos);
        if (updatedState.isAir()) {
            return updatedState;
        }
        if (!updatedState.is(this)) {
            return updatedState;
        }

        DoubleBlockHalf half = updatedState.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return neighborState.is(this) && neighborState.getValue(HALF) != half
                    ? updatedState.setValue(SlidingDoorBlock.VISIBLE, neighborState.getValue(SlidingDoorBlock.VISIBLE))
                    : Blocks.AIR.defaultBlockState();
        }

        return updatedState;
    }

    @Override
    public void setOpen(@Nullable Entity entity, Level level, BlockState state, BlockPos pos, boolean open) {
        if (!state.is(this) || state.getValue(OPEN) == open) {
            return;
        }

        BlockState changedState = state.setValue(OPEN, open);
        if (open) {
            changedState = changedState.setValue(SlidingDoorBlock.VISIBLE, false);
        }
        level.setBlock(pos, changedState, Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);

        DoorHingeSide hinge = changedState.getValue(HINGE);
        Direction facing = changedState.getValue(FACING);
        BlockPos otherPos = pos.relative(hinge == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise());
        BlockState otherDoor = level.getBlockState(otherPos);
        if (SlidingDoorBlock.isDoubleDoor(changedState, hinge, facing, otherDoor)) {
            setOpen(entity, level, otherDoor, otherPos, open);
        }

        this.playSound(entity, level, pos, open);
        level.gameEvent(entity, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        boolean lower = state.getValue(HALF) == DoubleBlockHalf.LOWER;
        boolean powered = isDoorPowered(level, pos, state);
        if (defaultBlockState().is(block) || powered == state.getValue(POWERED)) {
            return;
        }

        SlidingEntranceRiftBlockEntity blockEntity = getSlidingBlockEntity(level, lower ? pos : pos.below());
        if (blockEntity != null && blockEntity.deferUpdate) {
            return;
        }

        BlockState changedState = state.setValue(POWERED, powered)
                .setValue(OPEN, powered);
        if (powered) {
            changedState = changedState.setValue(SlidingDoorBlock.VISIBLE, false);
        }

        if (powered != state.getValue(OPEN)) {
            this.playSound(null, level, pos, powered);
            level.gameEvent(null, powered ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);

            DoorHingeSide hinge = changedState.getValue(HINGE);
            Direction facing = changedState.getValue(FACING);
            BlockPos otherPos = pos.relative(hinge == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise());
            BlockState otherDoor = level.getBlockState(otherPos);

            if (SlidingDoorBlock.isDoubleDoor(changedState, hinge, facing, otherDoor)) {
                otherDoor = otherDoor.setValue(POWERED, powered)
                        .setValue(OPEN, powered);
                if (powered) {
                    otherDoor = otherDoor.setValue(SlidingDoorBlock.VISIBLE, false);
                }
                level.setBlock(otherPos, otherDoor, Block.UPDATE_CLIENTS);
            }
        }

        level.setBlock(pos, changedState, Block.UPDATE_CLIENTS);
        if (!level.isClientSide && changedState.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        state = state.cycle(OPEN);
        boolean open = state.getValue(OPEN);
        if (open) {
            state = state.setValue(SlidingDoorBlock.VISIBLE, false);
        }
        level.setBlock(pos, state, Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);
        if (!level.isClientSide && state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        level.gameEvent(player, isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);

        DoorHingeSide hinge = state.getValue(HINGE);
        Direction facing = state.getValue(FACING);
        BlockPos otherPos = pos.relative(hinge == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise());
        BlockState otherDoor = level.getBlockState(otherPos);
        if (SlidingDoorBlock.isDoubleDoor(state, hinge, facing, otherDoor)) {
            useWithoutItem(otherDoor, level, otherPos, player, hitResult);
        } else if (open) {
            this.playSound(player, level, pos, true);
            level.gameEvent(player, GameEvent.BLOCK_OPEN, pos);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void closeDoorBehind(Level level, BlockPos top, BlockPos bottom) {
        closeDoorHalfBehind(level, top);
        closeDoorHalfBehind(level, bottom);
    }

    private void closeDoorHalfBehind(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(this)) {
            return;
        }

        BlockState closedState = state.setValue(OPEN, false)
                .setValue(SlidingDoorBlock.VISIBLE, true);
        level.setBlock(pos, closedState, Block.UPDATE_ALL);
        if (!level.isClientSide && closedState.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            return null;
        }
        return new SlidingEntranceRiftBlockEntity(pos, state);
    }

    @Override
    public EntranceRiftBlockEntity getRift(Level world, BlockPos pos, BlockState state) {
        BlockPos riftPos = state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        return world.getBlockEntity(riftPos, CreateCompatBlockEntityTypes.SLIDING_ENTRANCE_RIFT)
                .orElseThrow(() -> new IllegalStateException("Sliding dimensional door at " + pos + " in world " + world + " contained no rift."));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return DimensionalPortalBlock.checkType(type, CreateCompatBlockEntityTypes.SLIDING_ENTRANCE_RIFT, (level, blockPos, blockState, blockEntity) -> blockEntity.tick(level, blockPos, blockState));
    }

    private void deferUpdate(LevelAccessor level, BlockPos pos) {
        if (level instanceof Level concreteLevel) {
            SlidingEntranceRiftBlockEntity blockEntity = getSlidingBlockEntity(concreteLevel, pos);
            if (blockEntity != null) {
                blockEntity.deferUpdate = true;
            }
        }
    }

    @Nullable
    private SlidingEntranceRiftBlockEntity getSlidingBlockEntity(Level level, BlockPos pos) {
        return level.getBlockEntity(pos, CreateCompatBlockEntityTypes.SLIDING_ENTRANCE_RIFT)
                .orElse(null);
    }

    private static boolean isDoorPowered(Level level, BlockPos pos, BlockState state) {
        boolean lower = state.getValue(HALF) == DoubleBlockHalf.LOWER;
        DoorHingeSide hinge = state.getValue(HINGE);
        Direction facing = state.getValue(FACING);
        BlockPos otherPos = pos.relative(hinge == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise());
        BlockState otherDoor = level.getBlockState(otherPos);

        if (SlidingDoorBlock.isDoubleDoor(state.cycle(OPEN), hinge, facing, otherDoor) && (level.hasNeighborSignal(otherPos)
                || level.hasNeighborSignal(otherPos.relative(lower ? Direction.UP : Direction.DOWN)))) {
            return true;
        }

        return level.hasNeighborSignal(pos)
                || level.hasNeighborSignal(pos.relative(lower ? Direction.UP : Direction.DOWN));
    }
}
