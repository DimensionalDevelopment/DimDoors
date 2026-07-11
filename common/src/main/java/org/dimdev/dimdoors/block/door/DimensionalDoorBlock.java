package org.dimdev.dimdoors.block.door;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.math.MathUtil;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.dimdoors.block.*;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.rift.RiftUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static net.minecraft.world.level.material.PushReaction.BLOCK;

public abstract class DimensionalDoorBlock<T extends EntranceRiftBlockEntity> extends WaterLoggableDoorBlock implements TraversableRiftBlock<T> {
    public DimensionalDoorBlock(Properties settings, BlockSetType blockSetType) {
        super(settings.pushReaction(BLOCK), blockSetType);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Entity entity) {
        TraversableRiftBlock.super.entityInside(state, world, pos, entity);
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        TraversableRiftBlock.super.onBlockExploded(state, level, pos, explosion);
    }

    @Override
    public boolean validStateForTraversal(BlockState state) {
        return state.getBlock() != this || !state.getValue(DoorBlock.OPEN);
    }

    @Override
    public void postTraverseEffect(Level level, BlockPos pos, BlockState state, RiftBlockEntity rift) {
        closeDoorBehind(level, pos);
        closeDoorBehind(level, pos.above());
    }

    protected void closeDoorBehind(Level world, BlockPos pos) {
        world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(DoorBlock.OPEN, false));


    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        state = state.cycle(OPEN);
        world.setBlock(pos, state, 10);
        if (!world.isClientSide && state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        this.playSound(player, world, pos, state.getValue(OPEN));
        world.gameEvent(player, this.isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canBeReplaced(@NotNull BlockState blockState, @NotNull BlockPlaceContext blockPlaceContext) {
        return super.canBeReplaced(blockState, blockPlaceContext) || blockState.getBlock() == ModBlocks.DETACHED_RIFT;
    }

    @Nullable
    @Override
    public T newBlockEntity(@NotNull BlockPos pos, BlockState state) {
        return state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER ? null : getRiftBlockEnityType().create(pos, state);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder params) {
        state = getVisualBlockState(state);

        return state.getDrops(params);
    }

    @Override
    public BlockPos getRiftPos(Level world, BlockPos pos, BlockState state) {
        return state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
    }

    @Override
    public String providerType() {
        return "Dimensional door";
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return neighborState.getBlock() instanceof DoorBlock && neighborState.getValue(HALF) != doubleBlockHalf ? neighborState.setValue(HALF, doubleBlockHalf) : Blocks.AIR.defaultBlockState();
        } else {
            return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(world, pos) ? ModBlocks.DETACHED_RIFT.defaultBlockState() : state;
        }
    }

    @Override
    public @NotNull VoxelShape getInteractionShape(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos) {
        return Shapes.block();
    }

    @Override
    public TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder(BlockState state, BlockPos pos) {
        return TransformationMatrix3d.builder()
                .inverseTranslate(Vec3.atCenterOf(pos).add(Vec3.atLowerCornerOf(state.getValue(DoorBlock.FACING).getNormal()).scale(-0.31)))
                .inverseRotate(MathUtil.directionEulerAngle(state.getValue(DoorBlock.FACING).getOpposite()));
    }

    @Override
    public TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder(BlockState state, BlockPos pos) {
        return TransformationMatrix3d.builder()
                .inverseRotate(MathUtil.directionEulerAngle(state.getValue(DoorBlock.FACING).getOpposite()));
    }


    @Override
    public boolean isExitFlipped() {
        return true;
    }

    @Override
    public boolean isTall(BlockState cachedState) {
        return true;
    }

    @Override
    public boolean stateContainsRift(BlockState oldState) {
        return oldState.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
    }

    public Block baseBlock() {
        return BuiltInRegistries.BLOCK.get(DimensionalDoors.getDimensionalDoorBlockRegistrar().get(BuiltInRegistries.BLOCK.getKey(this)));
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public Optional<T> convertToRiftProvider(ServerLevel world, BlockPos pos, BlockState state) {
        return Optional.of(getRift(world, pos, state));
    }

    @Override
    public void revertToBaseVariant(ServerLevel level, BlockPos pos, BlockState state) {
        state = getVisualBlockState(state);

        BlockPos upperPos;
        BlockState upperState;
        BlockPos lowerPos;
        BlockState lowerState;

        if(state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            upperPos = pos;
            upperState = state;
            lowerPos = pos.below();
            lowerState = state.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER);
        } else {
            upperPos = pos.above();
            upperState = state.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
            lowerPos = pos;
            lowerState = state;
        }

        level.setBlock(lowerPos, lowerState, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        level.setBlock(upperPos, upperState, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
    }

    @Override
    public RiftUtils.PortalPlane getPortalPlane(BlockState state, BlockPos pos) {
        return RiftUtils.PortalPlane.ofDoor(state, pos);
    }

    @Override
    public void closeRift(Level level, BlockPos pos, BlockState state) {
        var base = baseBlock();

        if (base instanceof DoorBlock doorBlock) {
            var newState = doorBlock.defaultBlockState()
                    .setValue(FACING, state.getValue(FACING))
                    .setValue(OPEN, state.getValue(OPEN))
                    .setValue(HINGE, state.getValue(HINGE))
                    .setValue(POWERED, state.getValue(POWERED))
                    .setValue(HALF, DoubleBlockHalf.LOWER);

            level.removeBlock(pos, false);
            level.setBlockAndUpdate(pos, newState);
            level.setBlockAndUpdate(pos.above(), newState.setValue(HALF, DoubleBlockHalf.UPPER));
        }
    }
}
