package org.dimdev.dimdoors.api.event

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import org.dimdev.dimcore.api.util.SimpleEvent

interface UseItemOnBlockCallback {
    fun useItemOnBlock(
        player: Player,
        world: Level,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): InteractionResult

    companion object {
        @JvmField
        val EVENT: SimpleEvent<UseItemOnBlockCallback> = SimpleEvent.of { listeners ->
            object : UseItemOnBlockCallback {
                override fun useItemOnBlock(
                    player: Player,
                    world: Level,
                    hand: InteractionHand,
                    hitResult: BlockHitResult
                ): InteractionResult {
                    for (listener in listeners) {
                        val result = listener.useItemOnBlock(player, world, hand, hitResult)
                        if (result != InteractionResult.PASS) {
                            return result
                        }
                    }
                    return InteractionResult.PASS
                }
            }
        }
    }
}
