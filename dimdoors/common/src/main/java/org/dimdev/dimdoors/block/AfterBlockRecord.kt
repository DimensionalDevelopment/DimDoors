package org.dimdev.dimdoors.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

data class AfterBlockRecord(val state: BlockState, val world: Level, val pos: BlockPos)
