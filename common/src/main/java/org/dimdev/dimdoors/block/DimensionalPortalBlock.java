package org.dimdev.dimdoors.block;

import com.mojang.serialization.MapCodec;
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
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.block.AfterMoveCollidableBlock;
import org.dimdev.dimdoors.api.block.ExplosionConvertibleBlock;
import org.dimdev.dimdoors.api.entity.LastPositionProvider;
import org.dimdev.dimdoors.api.util.math.MathUtil;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class DimensionalPortalBlock extends WaterLoggableBlockWithEntity implements RiftProvider<EntranceRiftBlockEntity>, CoordinateTransformerBlock, ExplosionConvertibleBlock, AfterMoveCollidableBlock, CustomBreakHandling {
    public static final MapCodec<DimensionalPortalBlock> CODEC = simpleCodec(DimensionalPortalBlock::new);

    public static DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public DimensionalPortalBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
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
        var rift = this.getRift(world, pos, state);

        if (!rift.hasTraversed(world, previousPos, currentPos)) {
            // The movement did not cross the active portal plane.
            return InteractionResult.PASS;
        }

        // TODO: replace with dimdoor cooldown?
        if (entity.isOnPortalCooldown()) {
            entity.setPortalCooldown();
            return InteractionResult.PASS;
        }
        entity.setPortalCooldown();

        rift.teleport(entity);

        createDetachedRift(world, pos);
        
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        return super.canBeReplaced(blockState, blockPlaceContext) || blockState.getBlock() == ModBlocks.DETACHED_RIFT;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EntranceRiftBlockEntity(pos, state);
    }

    public static void createDetachedRift(Level world, BlockPos pos) {
        createDetachedRift(world, pos, world.getBlockState(pos));
    }

    public static void createDetachedRift(Level world, BlockPos pos, BlockState state) {
        var blockEntity = world.getBlockEntity(pos, ModBlockEntityTypes.ENTRANCE_RIFT);

        if(blockEntity.isPresent()) {
            world.setBlockAndUpdate(pos, ModBlocks.DETACHED_RIFT.defaultBlockState().setValue(WATERLOGGED, state.getValue(WATERLOGGED)));
            world.getBlockEntity(pos, ModBlockEntityTypes.DETACHED_RIFT).ifPresent(a -> a.setData(blockEntity.get().getData()));
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
        return world.getBlockEntity(pos, ModBlockEntityTypes.ENTRANCE_RIFT).orElseThrow(() -> new IllegalStateException("Dimensional door at " + pos + " in world " + world + " contained no rift."));
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
    public TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder(BlockState state, BlockPos pos) {
        return TransformationMatrix3d.builder()
                .inverseTranslate(Vec3.atCenterOf(pos).add(Vec3.atLowerCornerOf(state.getValue(HorizontalDirectionalBlock.FACING).getNormal()).scale(-0.31)))
                .inverseRotate(MathUtil.directionEulerAngle(state.getValue(HorizontalDirectionalBlock.FACING).getOpposite()));
    }

    @Override
    public TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder(BlockState state, BlockPos pos) {
        return TransformationMatrix3d.builder()
                .inverseRotate(MathUtil.directionEulerAngle(state.getValue(HorizontalDirectionalBlock.FACING).getOpposite()));
    }

    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    protected BlockState mirror(BlockState state, Mirror mirror) {
        return mirror == Mirror.NONE ? state : state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean isExitFlipped() {
        return true;
    }

    @Override
    public boolean isTall(BlockState cachedState) {
        return true;
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

    @Nullable
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return createTickerHelper(givenType, expectedType, ticker);
    }
}
