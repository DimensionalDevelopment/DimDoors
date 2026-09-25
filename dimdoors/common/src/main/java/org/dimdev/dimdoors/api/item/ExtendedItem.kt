package org.dimdev.dimdoors.api.item

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import org.dimdev.dimdoors.api.item.AttackBlockResult.Companion.fail

interface ExtendedItem {
    // TODO: add javadocs
    // true -> send packet to server
    // false -> don't send packet to server
    // boolean value currently does nothing server-side
    fun onAttackBlock(
        world: Level,
        player: Player,
        hand: InteractionHand,
        pos: BlockPos,
        direction: Direction
    ): AttackBlockResult {
        return fail(false)
    }
}
