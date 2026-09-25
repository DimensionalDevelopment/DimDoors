package org.dimdev.dimdoors.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

interface CustomBreakHandling {
    fun customDestroy(level: Level, pos: BlockPos, state: BlockState, i: Int, j: Int): Boolean?
}
