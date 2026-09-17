package org.dimdev.dimdoors.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.entity.Rift;

import java.util.Optional;

public interface RiftVariantProvider {
    Optional<? extends Rift> convertToRiftProvider(ServerLevel world, BlockPos pos, BlockState state);

    default Optional<BlockState> getRiftProviderState(BlockState state) {
        return Optional.empty();
    }

    default void revertToBaseVariant(ServerLevel world, BlockPos pos, BlockState state) {}
}
