package org.dimdev.dimcore.api

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level

fun interface PlacedContentTracker {
    fun isPlaced(level: Level, pos: BlockPos): Boolean
    fun onPlaced(level: ServerLevel, pos: BlockPos) {}
    fun onRemoved(level: ServerLevel, pos: BlockPos) {}
}

object PlacedContent {
    @JvmStatic var tracker: PlacedContentTracker = PlacedContentTracker { _, _ -> false }

    @JvmStatic fun isPlaced(level: Level, pos: BlockPos) = tracker.isPlaced(level, pos)
}
