package org.dimdev.dimdoors.listener

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import org.dimdev.dimcore.api.Platform
import org.dimdev.dimdoors.api.item.ExtendedItem
import org.dimdev.dimdoors.network.client.ClientPacketListener.tryToSendPacket
import org.dimdev.dimdoors.network.packet.c2s.HitBlockWithItemC2SPacket

class AttackBlockCallbackListener : Platform.AttackBlockCallback {
    override fun attack(player: Player, hand: InteractionHand, pos: BlockPos, direction: Direction): InteractionResult {
        val world = player.level()

        if (!world.isClientSide) return InteractionResult.PASS
        val item = player.getItemInHand(hand).item
        if (item !is ExtendedItem) {
            return InteractionResult.PASS
        }

        val result = item.onAttackBlock(world, player, hand, pos, direction)
        if (result.sendPacket) {
            if (!tryToSendPacket(HitBlockWithItemC2SPacket(hand, pos, direction))) {
                return InteractionResult.FAIL
            }
        }

        return result.result!!
    }
}
