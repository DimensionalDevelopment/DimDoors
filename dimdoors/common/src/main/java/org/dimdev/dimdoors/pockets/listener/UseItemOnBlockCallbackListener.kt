package org.dimdev.dimdoors.pockets.listener

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback
import org.dimdev.dimdoors.pockets.addon.PocketAddons
import org.dimdev.dimdoors.pockets.listener.PocketListenerUtil.getAddon

class UseItemOnBlockCallbackListener : UseItemOnBlockCallback {
    override fun useItemOnBlock(
        player: Player,
        world: Level,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): InteractionResult {
        return getAddon(PocketAddons.PREVENT_BLOCK_MODIFICATION_ADDON, world, player.blockPosition()
        )?.useItemOnBlock(player, world, hand, hitResult)?.takeIf { it !== InteractionResult.PASS } ?: InteractionResult.PASS
    }
}
