package org.dimdev.dimdoors.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public interface AfterMoveCollidableBlock {
	// only triggers on servers
	InteractionResult onAfterMovePlayerCollision(BlockState state, ServerLevel world, BlockPos pos, ServerPlayer player, Vec3 positionChange);
}
