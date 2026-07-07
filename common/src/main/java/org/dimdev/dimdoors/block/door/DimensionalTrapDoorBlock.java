package org.dimdev.dimdoors.block.door;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
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
import static org.dimdev.dimdoors.block.DimensionalPortalBlock.checkType;

public abstract class DimensionalTrapDoorBlock<T extends EntranceRiftBlockEntity> extends TrapDoorBlock implements TraversableRiftBlock<T> {
    public DimensionalTrapDoorBlock(Properties settings, BlockSetType blockSetType) {
        super(blockSetType, settings.pushReaction(BLOCK));
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
    public RiftUtils.PortalPlane getPortalPlane(BlockState state, BlockPos pos) {
        return RiftUtils.PortalPlane.ofTrapdoor(state, pos);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        state = state.cycle(OPEN);
        world.setBlock(pos, state, 10);
        if (!world.isClientSide && state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        this.playSound(player, world, pos, state.getValue(OPEN));
        world.gameEvent(player, state.getValue(OPEN) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canBeReplaced(@NotNull BlockState blockState, @NotNull BlockPlaceContext blockPlaceContext) {
        return super.canBeReplaced(blockState, blockPlaceContext) || blockState.getBlock() == ModBlocks.DETACHED_RIFT;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder params) {
        state = getEffectiveBlockState(state);

        return state.getDrops(params);
    }

    @Override
    public @NotNull VoxelShape getInteractionShape(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos) {
        return Shapes.block();
    }

    @Override
    public TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder(BlockState state, BlockPos pos) {
        return TransformationMatrix3d.builder()
                .inverseTranslate(Vec3.atCenterOf(pos.above()));
    }

    @Override
    public TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder(BlockState state, BlockPos pos) {
        return TransformationMatrix3d.builder()
                .inverseRotate(MathUtil.directionEulerAngle(state.getValue(DoorBlock.FACING)));
    }


    public BlockState getEffectiveBlockState(BlockState state) {
        return state;
    }

    @Override
    public @Nullable <R extends BlockEntity> BlockEntityTicker<R> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<R> blockEntityType) {
        return checkType(blockEntityType, getRiftBlockEnityType(), RiftProvider::tickRift);
    }

    public Block baseBlock() {
        return BuiltInRegistries.BLOCK.get(DimensionalDoors.getDimensionalDoorBlockRegistrar().get(BuiltInRegistries.BLOCK.getKey(this)));
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public Optional<RiftBlockEntity> convertToRiftProvider(ServerLevel world, BlockPos pos, BlockState state) {
        return Optional.of(getRift(world, pos, state));
    }

    @Override
    public void closeRift(Level level, BlockPos pos, BlockState state) {
        var base = baseBlock();

        if (base instanceof TrapDoorBlock doorBlock) {
            var newState = doorBlock.defaultBlockState()
                    .setValue(FACING, state.getValue(FACING))
                    .setValue(OPEN, state.getValue(OPEN))
                    .setValue(POWERED, state.getValue(POWERED))
                    .setValue(TrapDoorBlock.HALF, state.getValue(TrapDoorBlock.HALF));

            level.removeBlock(pos, false);
            level.setBlockAndUpdate(pos, newState);
        }
    }
}
