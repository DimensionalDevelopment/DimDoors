package org.dimdev.dimdoors.api.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

interface AfterMoveCollidableBlock {
    fun onAfterMovePlayerCollision(
        state: BlockState,
        world: ServerLevel,
        pos: BlockPos,
        player: ServerPlayer,
        previousPos: Vec3,
        currentPos: Vec3
    )
}
