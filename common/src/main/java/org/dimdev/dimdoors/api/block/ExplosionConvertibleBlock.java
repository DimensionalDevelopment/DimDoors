package org.dimdev.dimdoors.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ExplosionConvertibleBlock {
	ActionResult explode(World world, BlockPos pos, BlockState state, BlockEntity blockEntity);
}
