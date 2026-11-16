package org.dimdev.dimdoors.util

import net.minecraft.core.BlockPos
import java.util.function.Function

object BlockPosUtil {
    fun <T> nearbyVertical(pos: BlockPos, lookup: (BlockPos) ->  T?): T? = pos.let(lookup) ?: pos.below().let(lookup) ?: pos.above().let(lookup)
}