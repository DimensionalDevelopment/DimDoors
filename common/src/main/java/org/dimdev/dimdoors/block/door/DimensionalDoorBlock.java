package org.dimdev.dimdoors.block.door;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
import org.dimdev.dimdoors.api.block.AfterMoveCollidableBlock;
import org.dimdev.dimdoors.api.block.ExplosionConvertibleBlock;
import org.dimdev.dimdoors.api.entity.LastPositionProvider;
import org.dimdev.dimdoors.api.util.math.MathUtil;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.dimdoors.block.CoordinateTransformerBlock;
import org.dimdev.dimdoors.block.CustomBreakHandling;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static net.minecraft.world.level.material.PushReaction.BLOCK;
import static org.dimdev.dimdoors.block.DimensionalPortalBlock.Dummy.checkType;

public class DimensionalDoorBlock extends WaterLoggableDoorBlock implements RiftProvider<EntranceRiftBlockEntity>, CoordinateTransformerBlock, ExplosionConvertibleBlock, AfterMoveCollidableBlock, CustomBreakHandling {
    public DimensionalDoorBlock(Properties settings, BlockSetType blockSetType) {
        super(settings.pushReaction(BLOCK), blockSetType);
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (world.isClientSide || entity instanceof ServerPlayer) {
            return;
        }

        onCollision(state, world, pos, entity, ((LastPositionProvider) entity).getLastPos(), entity.position());
    }


    @Override
    public InteractionResult onAfterMovePlayerCollision(BlockState state, ServerLevel world, BlockPos pos, ServerPlayer player, Vec3 previousPos, Vec3 currentPos) {
        return onCollision(state, world, pos, player, previousPos, currentPos);
    }

    private InteractionResult onCollision(BlockState state, Level world, BlockPos pos, Entity entity, Vec3 previousPos, Vec3 currentPos) {
        BlockPos top = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos : pos.above();
        BlockPos bottom = top.below();
        BlockState doorState = world.getBlockState(bottom);

        // TODO: decide whether door should need to be open for teleportation
        if (doorState.getBlock() != this || !doorState.getValue(DoorBlock.OPEN)) { // '== this' to check if not half-broken
            return InteractionResult.PASS;
        }

        var rift = this.getRift(world, pos, state);

        if (rift.hasTraversed(world, previousPos, currentPos)) {
            // intersection is outside of plane width/ height
            return InteractionResult.PASS;
        }

        // TODO: replace with dimdoor cooldown?
        if (entity.isOnPortalCooldown()) {
            entity.setPortalCooldown();
            return InteractionResult.PASS;
        }
        entity.setPortalCooldown();

        rift.teleport(entity);

        if (DimensionalDoors.getConfig().getDoorsConfig().closeDoorBehind) {
            world.setBlockAndUpdate(top, world.getBlockState(top).setValue(DoorBlock.OPEN, false));
            world.setBlockAndUpdate(bottom, world.getBlockState(bottom).setValue(DoorBlock.OPEN, false));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
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
    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        return super.canBeReplaced(blockState, blockPlaceContext) || blockState.getBlock() == ModBlocks.DETACHED_RIFT;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            return null;
        }
        return new EntranceRiftBlockEntity(pos, state);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        state = getEffectiveBlockState(state);

        return state.getDrops(params);
    }

    public static void createDetachedRift(Level world, BlockPos pos) {
        createDetachedRift(world, pos, world.getBlockState(pos));
    }

    /*
     TODO: rewrite so it can only be used from the lower door block.
      I fear this method may be called twice otherwise.
      ~CreepyCre
     */
    public static void createDetachedRift(Level world, BlockPos pos, BlockState state) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
        BlockPos blockPos = pos;
        BlockState blockState = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
            blockPos = pos.below();
            blockState = world.getBlockState(blockPos);
            blockEntity = world.getBlockEntity(blockPos);
        }
        if (blockEntity instanceof EntranceRiftBlockEntity
                && blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
            world.setBlockAndUpdate(blockPos, ModBlocks.DETACHED_RIFT.defaultBlockState().setValue(WATERLOGGED, blockState.getValue(WATERLOGGED)));
            ((DetachedRiftBlockEntity) world.getBlockEntity(blockPos)).setData(((EntranceRiftBlockEntity) blockEntity).getData());
        }
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        BlockEntity entity = level.getBlockEntity(pos);

        if (entity instanceof EntranceRiftBlockEntity riftBlockEntity) {
            level.setBlock(pos, ModBlocks.DETACHED_RIFT.defaultBlockState(), 3);

            level.getBlockEntity(pos, ModBlockEntityTypes.DETACHED_RIFT).ifPresent(detachedRiftBlockEntity -> detachedRiftBlockEntity.copyFrom(riftBlockEntity));
        }
    }

    @Override
    public EntranceRiftBlockEntity getRift(Level world, BlockPos pos, BlockState state) {
        BlockEntity bottomEntity;
        BlockEntity topEntity;

        if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
            bottomEntity = world.getBlockEntity(pos);
            topEntity = world.getBlockEntity(pos.above());
        } else {
            bottomEntity = world.getBlockEntity(pos.below());
            topEntity = world.getBlockEntity(pos);
        }

        // TODO: Also notify player in case of error, don't crash
        if (bottomEntity instanceof EntranceRiftBlockEntity && topEntity instanceof EntranceRiftBlockEntity) {
            LOGGER.warn("Dimensional door at " + pos + " in world " + world + " contained two rifts, please report this. Defaulting to bottom.");
            return (EntranceRiftBlockEntity) bottomEntity;
        } else if (bottomEntity instanceof EntranceRiftBlockEntity) {
            return (EntranceRiftBlockEntity) bottomEntity;
        } else if (topEntity instanceof EntranceRiftBlockEntity) {
            return (EntranceRiftBlockEntity) topEntity;
        } else {
            throw new IllegalStateException("Dimensional door at " + pos + " in world " + world + " contained no rift.");
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        DoubleBlockHalf doubleBlockHalf = (DoubleBlockHalf) state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return neighborState.getBlock() instanceof DoorBlock && neighborState.getValue(HALF) != doubleBlockHalf ? (BlockState) neighborState.setValue(HALF, doubleBlockHalf) : Blocks.AIR.defaultBlockState();
        } else {
            return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(world, pos) ? ModBlocks.DETACHED_RIFT.defaultBlockState() : state;
        }
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
//    DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
//
//    if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
//        BlockPos blockPos = pos.below();
//        BlockState blockState = world.getBlockState(blockPos);
//        BlockEntity blockEntity = world.getBlockEntity(blockPos);
//
//        if (blockEntity instanceof EntranceRiftBlockEntity
//            && blockState.getValue(HALF) == DoubleBlockHalf.LOWER
//            && !(player.isCreative()
//            && !DimensionalDoors.getConfig().getDoorsConfig().placeRiftsInCreativeMode)
//        ) {
//        world.setBlockAndUpdate(blockPos, ModBlocks.DETACHED_RIFT.defaultBlockState().setValue(WATERLOGGED, blockState.getValue(WATERLOGGED)));
//        ((DetachedRiftBlockEntity) world.getBlockEntity(blockPos)).setData(((EntranceRiftBlockEntity) blockEntity).getData());
//        }
//    }
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public Boolean customDestroy(Level level, BlockPos pos, BlockState state, int i, int j) {
        var blockEntity = level.getBlockEntity(pos);

        if (blockEntity == null) {
            return null;
        }
        createDetachedRift(level, pos);
        return true;
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        super.destroy(level, pos, state);
    }

    @Override
    public @NotNull VoxelShape getInteractionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
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

    public BlockState getEffectiveBlockState(BlockState state) {
        return state;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntityTypes.ENTRANCE_RIFT, (level, blockPos, blockState, blockEntity) -> blockEntity.tick(world, blockPos, blockState));
    }

    public Block baseBlock() {
        return BuiltInRegistries.BLOCK.get(DimensionalDoors.getDimensionalDoorBlockRegistrar().get(BuiltInRegistries.BLOCK.getKey(this)));
    }

    @Override
    protected RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public Optional<RiftBlockEntity> convertToRiftProvider(ServerLevel world, BlockPos pos, BlockState state) {
        return Optional.of(getRift(world, pos, state));
    }
}
