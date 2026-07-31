package org.dimdev.dimdoors.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.particle.client.RiftParticleOptions;
import org.dimdev.dimdoors.world.ModDimensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.dimdev.dimdoors.block.DimensionalPortalBlock.checkType;

public class DetachedRiftBlock extends WaterLoggableBlockWithEntity implements RiftProvider<DetachedRiftBlockEntity>, SimpleWaterloggedBlock {
    public static final MapCodec<DetachedRiftBlock> CODEC = simpleCodec(DetachedRiftBlock::new);

    public static final String ID = "rift";

    public DetachedRiftBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public DetachedRiftBlockEntity getRift(Level world, BlockPos pos, BlockState state) {
        return world.getBlockEntity(pos, ModBlockEntityTypes.DETACHED_RIFT).orElse(null);
    }

    @Override
    public void animateTick(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        // randomDisplayTick can be called before the tile entity is created in multiplayer
        if (!(blockEntity instanceof DetachedRiftBlockEntity rift)) return;

        boolean outsidePocket = !ModDimensions.isPocketDimension(world);
        double speed = 0.1;

        if (rift.getWeight() < 0) {
            world.addParticle(RiftParticleOptions.of(outsidePocket),
                    pos.getX() + .5,
                    pos.getY() + .5,
                    pos.getZ() + .5,
                    rand.nextGaussian() * speed,
                    rand.nextGaussian() * speed,
                    rand.nextGaussian() * speed
            );
        }

        world.addParticle(RiftParticleOptions.of(outsidePocket, rift.getWeight() == 0),
                pos.getX() + .5,
                pos.getY() + .5,
                pos.getZ() + .5,
                rand.nextGaussian() * speed,
                rand.nextGaussian() * speed,
                rand.nextGaussian() * speed
        );
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level world, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return checkType(type, ModBlockEntityTypes.DETACHED_RIFT, (level, blockPos, blockState, blockEntity) -> blockEntity.tick(world, blockPos, blockState));
    }

    @Override
    public BlockEntityType<DetachedRiftBlockEntity> getRiftBlockEnityType() {
        return ModBlockEntityTypes.DETACHED_RIFT;
    }

    @Override
    public String providerType() {
        return "Detached rift";
    }
}
