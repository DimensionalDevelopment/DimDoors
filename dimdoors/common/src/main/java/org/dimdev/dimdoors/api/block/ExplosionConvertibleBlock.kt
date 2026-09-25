package org.dimdev.dimdoors.api.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.api.cast

interface ExplosionConvertibleBlock {
    fun onBlockExploded(state: BlockState, level: Level, pos: BlockPos, explosion: Explosion) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3)
        this.cast<Block>().wasExploded(level, pos, explosion)
    }
}
