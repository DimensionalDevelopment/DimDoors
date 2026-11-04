package org.dimdev.dimdoors.listener

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import org.dimdev.dimcore.api.Platform
import org.dimdev.dimdoors.api.item.ExtendedItem

class AttackBlockCallbackListener : Platform.AttackBlockCallback {
    override fun attack(player: Player, hand: InteractionHand, pos: BlockPos, direction: Direction): InteractionResult {
        val world = player.level()

        val item = player.getItemInHand(hand).item
        if (item !is ExtendedItem) {
            return InteractionResult.PASS
        }

        return item.onAttackBlock(world, player, hand, pos, direction)
    }
}
