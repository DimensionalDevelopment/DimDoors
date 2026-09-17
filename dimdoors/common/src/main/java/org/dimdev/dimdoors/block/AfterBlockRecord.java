package org.dimdev.dimdoors.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public record AfterBlockRecord(BlockState state, Level world, BlockPos pos) {
}
