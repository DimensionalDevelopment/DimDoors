package org.dimdev.dimdoors.pockets.listener

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.api.Platform.BlockBreakCallback
import org.dimdev.dimdoors.pockets.addon.PocketAddons
import org.dimdev.dimdoors.pockets.listener.PocketListenerUtil.getAddon

class PlayerBlockBreakEventBeforeListener : BlockBreakCallback {
    override fun shouldCancel(level: Level, pos: BlockPos, state: BlockState, player: Player) = getAddon(PocketAddons.PREVENT_BLOCK_MODIFICATION_ADDON, level, pos)?.preventsBlockModification(player) ?: false
}
