package org.dimdev.dimdoors.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;

import java.util.Optional;

public interface RiftVariantProvider {
    Optional<? extends RiftBlockEntity> convertToRiftProvider(ServerLevel world, BlockPos pos);
}
