package org.dimdev.dimdoors.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public interface AfterMoveCollidableBlock {
	// only triggers on servers
	void onAfterMovePlayerCollision(BlockState state, ServerWorld world, BlockPos pos, ServerPlayerEntity player);
}
