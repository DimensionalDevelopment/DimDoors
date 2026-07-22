package org.dimdev.dimdoors.compat.create.block;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.PerservesBlockEntity;
import org.dimdev.dimdoors.block.entity.Rift;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.compat.create.CreateCompatBlockEntityTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LiminaCouplerBlock extends DirectionalKineticBlock implements IBE<LiminalCouplerBlockEntity>, PerservesBlockEntity {

    public LiminaCouplerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.block();
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!isPlacementTargetDetachedRift(context)) {
            return null;
        }

        Direction preferred = getPreferredFacing(context);
        if (preferred != null && (context.getPlayer() == null || !context.getPlayer().isShiftKeyDown())) {
            return defaultBlockState().setValue(FACING, preferred);
        }

        return super.getStateForPlacement(context);
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof LiminalCouplerBlockEntity coupler) {
            coupler.setDeleteRift(false);
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        RiftData data = null;
        if (!level.isClientSide && !isMoving && !state.is(newState.getBlock()) && !newState.is(ModBlocks.DETACHED_RIFT)
                && level.getBlockEntity(pos) instanceof LiminalCouplerBlockEntity coupler) {
            data = coupler.getData().copy();
            coupler.setDeleteRift(false);
        }

        super.onRemove(state, level, pos, newState, isMoving);

        if (data != null && level instanceof ServerLevel) {
            level.setBlock(pos, ModBlocks.DETACHED_RIFT.defaultBlockState(), Block.UPDATE_ALL);
            if (level.getBlockEntity(pos) instanceof Rift restoredRift) {
                restoredRift.setData(data);
                restoredRift.register();
                restoredRift.updateType();
            }
        }
    }

    @Override
    public boolean isCompatible(BlockState oldState) {
        return oldState.is(ModBlocks.DETACHED_RIFT);
    }

    @Override
    public void attemptTransfer(BlockEntity blockEntity, @Nullable BlockEntity blockEntityToBetransfered) {
        if (blockEntity instanceof LiminalCouplerBlockEntity coupler && blockEntityToBetransfered instanceof Rift rift) {
            coupler.copyFrom(rift);
            coupler.register();
        }
    }

    @Override
    public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hideStressImpact() {
        return true;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public Class<LiminalCouplerBlockEntity> getBlockEntityClass() {
        return LiminalCouplerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends LiminalCouplerBlockEntity> getBlockEntityType() {
        return CreateCompatBlockEntityTypes.LIMINAL_COUPLER;
    }

    private static boolean isPlacementTargetDetachedRift(BlockPlaceContext context) {
        return context.getLevel().getBlockState(context.getClickedPos()).is(ModBlocks.DETACHED_RIFT);
    }
}
