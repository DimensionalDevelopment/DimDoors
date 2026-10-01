package org.dimdev.dimdoors.listener.pocket

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback
import org.dimdev.dimdoors.listener.pocket.PocketListenerUtil.getAddon
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddons

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
