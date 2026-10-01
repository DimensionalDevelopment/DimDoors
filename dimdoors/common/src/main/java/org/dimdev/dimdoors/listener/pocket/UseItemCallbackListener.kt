package org.dimdev.dimdoors.listener.pocket

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import org.dimdev.dimcore.api.Platform
import org.dimdev.dimdoors.listener.pocket.PocketListenerUtil.getAddon
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddons

class UseItemCallbackListener : Platform.UseItemCallback {
    override fun use(player: Player, hand: InteractionHand): InteractionResult {
        val world = player.level()
        return getAddon(PocketAddons.PREVENT_BLOCK_MODIFICATION_ADDON, world, player.blockPosition())?.useItem(
            player, hand).takeIf { it !== InteractionResult.PASS } ?: InteractionResult.PASS
    }
}
