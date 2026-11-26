package org.dimdev.dimdoors.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimdoors.decay.Decay.decayBlock
import org.dimdev.dimdoors.decay.DecaySource

class RealitySpongeBlock(settings: Properties) : Block(settings) {
    public override fun randomTick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        for (direction in Direction.entries) {
            val targetBlockPos = pos.relative(direction)
            decayBlock(
                world,
                DecaySource.REALITY_SPONGE,
                targetBlockPos,
            )
        }
    }
}
