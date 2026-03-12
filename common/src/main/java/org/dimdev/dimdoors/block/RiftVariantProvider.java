package org.dimdev.dimdoors.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface RiftVariantProvider {
    Optional<? extends RiftBlockEntity> convertToRiftProvider(ServerLevel world, BlockPos pos, BlockState state);
}
