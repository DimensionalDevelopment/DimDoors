package org.dimdev.dimdoors.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.math.MathUtil;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.Rift;
import org.dimdev.dimdoors.rift.RiftUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DimensionalPortalBlock extends WaterLoggableBlockWithEntity implements TraversableRiftBlock<EntranceRiftBlockEntity> {
    public static final MapCodec<DimensionalPortalBlock> CODEC = simpleCodec(DimensionalPortalBlock::new);

    public static DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public DimensionalPortalBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        TraversableRiftBlock.super.entityInside(state, world, pos, entity);
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        TraversableRiftBlock.super.onBlockExploded(state, level, pos, explosion);
    }

    @Override
    public void postTraverseEffect(Level level, BlockPos pos, BlockState state, Rift rift) {
        rift.detach();
    }

    @Override
    public RiftUtils.PortalPlane getPortalPlane(BlockState state, BlockPos pos) {
        return RiftUtils.PortalPlane.ofDoor(state, pos);
    }

    @Override
    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        return super.canBeReplaced(blockState, blockPlaceContext) || blockState.getBlock() == ModBlocks.DETACHED_RIFT;
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

    @Override
    public BlockEntityType<EntranceRiftBlockEntity> getRiftBlockEnityType() {
        return ModBlockEntityTypes.ENTRANCE_RIFT;
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
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return createTickerHelper(givenType, expectedType, ticker);
    }

    @Override
    public void closeRift(Level level, BlockPos pos, BlockState state) {
        level.removeBlock(pos, false);
    }
}
