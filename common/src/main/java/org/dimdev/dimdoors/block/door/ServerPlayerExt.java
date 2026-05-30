package org.dimdev.dimdoors.block.door;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ServerPlayerExt {
    void recordAfterBlockMove(BlockState state, Level world, BlockPos pos);
    public void playerBackAfterBlockMove();
    void setDimensionalDoorTeleport(boolean active);
    boolean isDimensionalDoorTeleport();
}
