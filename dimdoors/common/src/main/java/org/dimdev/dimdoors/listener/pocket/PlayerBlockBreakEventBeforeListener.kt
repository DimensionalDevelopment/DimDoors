package org.dimdev.dimdoors.listener.pocket

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.api.Platform.BlockBreakCallback
import org.dimdev.dimdoors.listener.pocket.PocketListenerUtil.getAddon
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddons

class PlayerBlockBreakEventBeforeListener : BlockBreakCallback {
    override fun shouldCancel(level: Level, pos: BlockPos, state: BlockState, player: Player) = getAddon(PocketAddons.PREVENT_BLOCK_MODIFICATION_ADDON, level, pos)?.preventsBlockModification(player) ?: false
}
