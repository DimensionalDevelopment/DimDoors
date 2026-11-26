package org.dimdev.dimdoors.api.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

interface ExtendedBlock {
    fun onAttacked(
        world: Level,
        player: Player,
        hand: InteractionHand,
        pos: BlockPos,
        direction: Direction
    ): InteractionResult {
        return InteractionResult.FAIL
    }
}
