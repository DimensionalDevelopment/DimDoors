package org.dimdev.dimdoors.pockets.listener

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import org.dimdev.dimcore.api.Platform
import org.dimdev.dimdoors.pockets.addon.PocketAddons
import org.dimdev.dimdoors.pockets.listener.PocketListenerUtil.getAddon

class UseItemCallbackListener : Platform.UseItemCallback {
    override fun use(player: Player, hand: InteractionHand): InteractionResult {
        val world = player.level()
        return getAddon(PocketAddons.PREVENT_BLOCK_MODIFICATION_ADDON, world, player.blockPosition())?.useItem(
            player, hand).takeIf { it !== InteractionResult.PASS } ?: InteractionResult.PASS
    }
}
