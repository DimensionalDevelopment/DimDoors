package org.dimdev.dimdoors.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.block.AfterMoveCollidableBlock;
import org.dimdev.dimdoors.api.block.ExplosionConvertibleBlock;
import org.dimdev.dimdoors.api.entity.LastPositionProvider;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.Rift;
import org.dimdev.dimdoors.rift.RiftUtils;

public interface TraversableRiftBlock<T extends EntranceRiftBlockEntity<?>> extends RiftProvider<T>, ExplosionConvertibleBlock, AfterMoveCollidableBlock, CustomBreakHandling, CoordinateTransformerBlock {

    default Boolean customDestroy(Level level, BlockPos pos, BlockState state, int i, int j) {
        var blockEntity = getRift(level, pos, state);

        if (blockEntity == null) {
            return null;
        }

        blockEntity.detach();
        return true;
    }

    default InteractionResult onCollision(BlockState state, Level world, BlockPos pos, Entity entity, Vec3 previousPos, Vec3 currentPos) {
        pos = getRiftPos(world, pos,state);

        state = world.getBlockState(pos);

        if (!validStateForTraversal(state)) return InteractionResult.PASS;

        var rift = this.getRift(world, pos, state);

        if (rift == null || rift.hasTraversed(world, previousPos, currentPos)) {
            return InteractionResult.PASS;
        }

        // TODO: replace with dimdoor cooldown?
        if (entity.isOnPortalCooldown()) {
            return InteractionResult.PASS;
        }

        entity.setPortalCooldown();

        if (!rift.teleport(entity)) {
            return InteractionResult.PASS;
        }

        postTraverseEffect(world, pos, state, rift);

        return InteractionResult.SUCCESS;
    }

    default void postTraverseEffect(Level level, BlockPos pos, BlockState state, Rift rift) {

    }

    default boolean validStateForTraversal(BlockState state) {
        return true;
    }

    RiftUtils.PortalPlane getPortalPlane(BlockState state, BlockPos pos);

    default BlockState getVisualBlockState(BlockState state) {
        return state;
    }

    void closeRift(Level level, BlockPos pos, BlockState state);

    @Override
    default void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        BlockEntity entity = level.getBlockEntity(pos);

        if (entity instanceof EntranceRiftBlockEntity riftBlockEntity) {
            level.setBlock(pos, ModBlocks.DETACHED_RIFT.defaultBlockState(), 3);

            level.getBlockEntity(pos, ModBlockEntityTypes.DETACHED_RIFT).ifPresent(detachedRiftBlockEntity -> detachedRiftBlockEntity.copyFrom(riftBlockEntity));
        }
    }

    default void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (world.isClientSide || entity instanceof ServerPlayer) {
            return;
        }

        onCollision(state, world, pos, entity, ((LastPositionProvider) entity).getLastPos(), entity.position());
    }



    default InteractionResult onAfterMovePlayerCollision(BlockState state, ServerLevel world, BlockPos pos, ServerPlayer player, Vec3 previousPos, Vec3 currentPos) {
        return onCollision(state, world, pos, player, previousPos, currentPos);
    }
}
