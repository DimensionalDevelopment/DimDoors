package org.dimdev.dimdoors.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface AfterMoveCollidableBlock {
	// only triggers on servers
	ActionResult onAfterMovePlayerCollision(BlockState state, ServerWorld world, BlockPos pos, ServerPlayerEntity player, Vec3d positionChange);
}
