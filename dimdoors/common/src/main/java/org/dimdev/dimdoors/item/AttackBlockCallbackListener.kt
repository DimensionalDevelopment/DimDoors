package org.dimdev.dimdoors.item

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import org.dimdev.dimcore.api.Platform
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.api.block.ExtendedBlock
import org.dimdev.dimdoors.api.item.ExtendedItem

class AttackBlockCallbackListener : Platform.AttackBlockCallback {
    override fun attack(player: Player, hand: InteractionHand, pos: BlockPos, direction: Direction): InteractionResult {
        val world = player.level()

        return player.getItemInHand(hand).item.castOrNull<ExtendedItem>()?.onAttackBlock(world, player, hand, pos, direction) ?: run {
            val state = world.getBlockState(pos)

            return state.block.castOrNull<ExtendedBlock>()?.onAttacked(world, player, hand, pos, direction) ?: InteractionResult.PASS
        }
    }
}
