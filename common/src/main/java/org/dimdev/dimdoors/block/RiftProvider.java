package org.dimdev.dimdoors.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.jetbrains.annotations.Nullable;

public interface RiftProvider<T extends RiftBlockEntity> extends EntityBlock, RiftVariantProvider, PerservesBlockEntity {
	T getRift(ServerLevel world, BlockPos pos, BlockState state);

    @Environment(EnvType.CLIENT)
	default boolean isTall(BlockState cachedState) {
		return false;
	}

    default boolean stateContainsRift(BlockState oldState) {
        return true;
    }

    @Override
    public default boolean isCompatible(BlockState oldState) {
        return oldState.getBlock() instanceof RiftProvider<?> riftProvider && riftProvider.stateContainsRift(oldState);
    }

    @Override
    default void attemptTransfer(BlockEntity blockEntity, @Nullable BlockEntity blockEntityToBetransfered) {
        if(blockEntity instanceof RiftBlockEntity rift1 && blockEntityToBetransfered instanceof RiftBlockEntity rift2) {
            rift1.copyFrom(rift2);
        }
    }
}
