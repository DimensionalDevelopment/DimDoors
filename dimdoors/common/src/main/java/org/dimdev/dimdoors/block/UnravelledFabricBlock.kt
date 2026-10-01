package org.dimdev.dimdoors.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimdoors.tag.ModWorldTags
import org.dimdev.dimdoors.util.TagUtils.isIn
import org.dimdev.dimdoors.world.decay.Decay.applySpreadDecay
import org.dimdev.dimdoors.world.decay.DecaySource

class UnravelledFabricBlock(settings: Properties) : Block(settings) {
    public override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        if (isIn(level, ModWorldTags.UNRAVELLED_FABRIC_CAN_UNRAVEL)) {
            applySpreadDecay(level, pos, random, DecaySource.LIMBO)
        }
    }
}
