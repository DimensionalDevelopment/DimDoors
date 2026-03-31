package org.dimdev.dimdoors.rift.targets;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;

public interface TickingTarget {
    <T extends RiftBlockEntity> void tick(final Level level, final BlockPos pos, final BlockState state, final RiftBlockEntity rift);
}
