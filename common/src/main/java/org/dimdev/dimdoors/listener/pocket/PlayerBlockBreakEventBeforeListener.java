package org.dimdev.dimdoors.listener.pocket;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.ISided;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

public class PlayerBlockBreakEventBeforeListener implements ISided.BlockBreakCallback {
    @Override
    public boolean shouldCancel(Level level, BlockPos pos, BlockState state, Player player) {
        return PocketListenerUtil.getAddon(PocketAddon.PocketAddonType.PREVENT_BLOCK_MODIFICATION_ADDON, level, pos)
                .map(addon -> addon.preventsBlockModification(player))
                .orElse(false);
    }
}
