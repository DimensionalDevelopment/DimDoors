package org.dimdev.dimcore.api.transfer

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType

class Lookup<H, K>(
    val key: K,
    private val finder: (K, Level, BlockPos, Direction?) -> H?,
    private val registrar: (K, BlockEntityType<*>, (BlockEntity, Direction?) -> H?) -> kotlin.Unit
) {
    fun find(level: Level, pos: BlockPos, side: Direction?): H? = finder(key, level, pos, side)

    fun register(type: BlockEntityType<*>, provider: (BlockEntity, Direction?) -> H?) = registrar(key, type, provider)
}
