package org.dimdev.dimdoors.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface ExplosionConvertibleBlock {
	InteractionResult explode(Level world, BlockPos pos, BlockState state, BlockEntity blockEntity);
}
