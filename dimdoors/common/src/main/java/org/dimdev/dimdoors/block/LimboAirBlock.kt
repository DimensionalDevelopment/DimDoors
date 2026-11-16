package org.dimdev.dimdoors.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.AirBlock
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimdoors.decay.Decay
import org.dimdev.dimdoors.decay.DecaySource
import org.dimdev.dimdoors.worldgen.ModDimensions.isLimboDimension

class LimboAirBlock(properties: Properties) : AirBlock(properties) {
    public override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        if (isLimboDimension(level)) Decay.applySpreadDecay(level, pos, random, DecaySource.LIMBO)
    }
}
