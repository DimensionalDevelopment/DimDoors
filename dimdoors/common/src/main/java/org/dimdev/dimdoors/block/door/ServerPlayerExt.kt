package org.dimdev.dimdoors.block.door

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

interface ServerPlayerExt {
    fun recordAfterBlockMove(state: BlockState?, world: Level?, pos: BlockPos?)
    fun playerBackAfterBlockMove()
}
