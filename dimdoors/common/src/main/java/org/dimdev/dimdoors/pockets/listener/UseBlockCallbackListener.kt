package org.dimdev.dimdoors.pockets.listener

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.BlockHitResult
import org.dimdev.dimcore.api.Platform
import org.dimdev.dimdoors.pockets.addon.PocketAddons
import org.dimdev.dimdoors.pockets.listener.PocketListenerUtil.getAddon

class UseBlockCallbackListener : Platform.UseBlockCallback {
    override fun use(player: Player, hand: InteractionHand, hitResult: BlockHitResult): InteractionResult {
        val world = player.level()
        return getAddon(
            PocketAddons.PREVENT_BLOCK_MODIFICATION_ADDON,
            world,
            hitResult.blockPos
        )?.useBlock(player, world, hand, hitResult)?.takeIf { it !== InteractionResult.PASS } ?: InteractionResult.PASS
    }
}
