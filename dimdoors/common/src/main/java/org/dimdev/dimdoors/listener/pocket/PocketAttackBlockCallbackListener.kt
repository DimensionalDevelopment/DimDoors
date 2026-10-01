package org.dimdev.dimdoors.listener.pocket

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import org.dimdev.dimcore.api.Platform
import org.dimdev.dimdoors.listener.pocket.PocketListenerUtil.getAddon
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddons

class PocketAttackBlockCallbackListener : Platform.AttackBlockCallback {
    override fun attack(
        player: Player,
        hand: InteractionHand,
        pos: BlockPos,
        direction: Direction
    ): InteractionResult {
        val level = player.level()
        return getAddon(PocketAddons.PREVENT_BLOCK_MODIFICATION_ADDON, level, pos)?.attackBlock(player, hand, pos, direction).takeIf { it !== InteractionResult.PASS } ?: InteractionResult.PASS

    }
}
