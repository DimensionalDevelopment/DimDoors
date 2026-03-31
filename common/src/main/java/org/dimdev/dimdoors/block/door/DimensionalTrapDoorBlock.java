package org.dimdev.dimdoors.block.door;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
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
import org.dimdev.dimdoors.api.block.AfterMoveCollidableBlock;
import org.dimdev.dimdoors.api.block.ExplosionConvertibleBlock;
import org.dimdev.dimdoors.api.entity.LastPositionProvider;
import org.dimdev.dimdoors.api.util.math.MathUtil;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.dimdoors.block.CoordinateTransformerBlock;
import org.dimdev.dimdoors.block.CustomBreakHandling;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static net.minecraft.world.level.material.PushReaction.BLOCK;
import static org.dimdev.dimdoors.block.DimensionalPortalBlock.Dummy.checkType;

public class DimensionalTrapDoorBlock extends TrapDoorBlock implements RiftProvider<EntranceRiftBlockEntity>, CoordinateTransformerBlock, ExplosionConvertibleBlock, AfterMoveCollidableBlock, CustomBreakHandling {
    public DimensionalTrapDoorBlock(BlockBehaviour.Properties settings, BlockSetType blockSetType) {
        super(blockSetType, settings.pushReaction(BLOCK));
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (world.isClientSide || entity instanceof ServerPlayer /* DO NOT REMOVE, THIS MAKES SURE onCollision IS CALLED IN onAfterMovePlayerCollision (fixes bug with anti-cheat)*/) {
            return;
        }
        onCollision(state, world, pos, entity, entity.position().subtract(((LastPositionProvider) entity).getLastPos()));
    }


    @Override
    public InteractionResult onAfterMovePlayerCollision(BlockState state, ServerLevel world, BlockPos pos, ServerPlayer player, Vec3 positionChange) {
        return onCollision(state, world, pos, player, positionChange);
    }

    private InteractionResult onCollision(BlockState state, Level world, BlockPos pos, Entity entity, Vec3 positionChange) {
        if (world instanceof ServerLevel level) {
            BlockState doorState = world.getBlockState(pos);

            // TODO: decide whether door should need to be open for teleportation
            if (doorState.getBlock() != this || !doorState.getValue(DoorBlock.OPEN)) { // '== this' to check if not half-broken
                return InteractionResult.PASS;
            }

            var rift = this.getRift(level, pos, state);

            if (rift.hasTraversed(entity, positionChange)) {
                // intersection is outside of plane width/ height
                return InteractionResult.PASS;
            }

            // TODO: replace with dimdoor cooldown?
            if (entity.isOnPortalCooldown()) {
                entity.setPortalCooldown();
                return InteractionResult.PASS;
            }
            entity.setPortalCooldown();


            rift.teleport(level.getServer(), entity);
            if (DimensionalDoors.getConfig().getDoorsConfig().closeDoorBehind) {
                world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(DoorBlock.OPEN, false));
            }

            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
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
    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        return super.canBeReplaced(blockState, blockPlaceContext) || blockState.getBlock() == ModBlocks.DETACHED_RIFT.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EntranceRiftBlockEntity(pos, state);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        state = getEffectiveBlockState(state);

        return state.getBlock().getDrops(state, params);
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
        BlockState blockState = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof EntranceRiftBlockEntity riftBlockEntity) {
            world.setBlockAndUpdate(pos, ModBlocks.DETACHED_RIFT.get().defaultBlockState().setValue(WATERLOGGED, blockState.getValue(WATERLOGGED)));
            world.getBlockEntity(pos, ModBlockEntityTypes.DETACHED_RIFT.get()).ifPresent(be -> be.copyFrom(riftBlockEntity));
        }
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        BlockEntity entity = level.getBlockEntity(pos);

        if(entity instanceof EntranceRiftBlockEntity riftBlockEntity) {
            level.setBlock(pos, ModBlocks.DETACHED_RIFT.get().defaultBlockState(), 3);

            level.getBlockEntity(pos, ModBlockEntityTypes.DETACHED_RIFT.get()).ifPresent(detachedRiftBlockEntity -> detachedRiftBlockEntity.copyFrom(riftBlockEntity));
        }
    }

    @Override
    public EntranceRiftBlockEntity getRift(ServerLevel world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = world.getBlockEntity(pos);


        // TODO: Also notify player in case of error, don't crash
        if (blockEntity instanceof EntranceRiftBlockEntity entranceRiftBlockEntity) {
            return entranceRiftBlockEntity;
        } else {
            throw new IllegalStateException("Dimensional door at " + pos + " in world " + world + " contained no rift.");
        }
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
                .inverseTranslate(Vec3.atCenterOf(pos.above()));
    }

    @Override
    public TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder(BlockState state, BlockPos pos) {
        return TransformationMatrix3d.builder()
                .inverseRotate(MathUtil.directionEulerAngle(state.getValue(DoorBlock.FACING)));
    }

    @Override
    public boolean isExitFlipped() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean isTall(BlockState cachedState) {
        return false;
    }

    public BlockState getEffectiveBlockState(BlockState state) {
        return state;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntityTypes.ENTRANCE_RIFT.get(), (level, blockPos, blockState, blockEntity) -> blockEntity.tick(world, blockPos, blockState));
    }

    public Block baseBlock() {
        return BuiltInRegistries.BLOCK.get(DimensionalDoors.getDimensionalDoorBlockRegistrar().get(BuiltInRegistries.BLOCK.getKey(this)));
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public Optional<RiftBlockEntity> convertToRiftProvider(ServerLevel world, BlockPos pos, BlockState state) {
        return Optional.of(getRift(world, pos, state));
    }
}
