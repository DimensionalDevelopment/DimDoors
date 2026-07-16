package org.dimdev.dimdoors.listener.pocket;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.dimdev.limlib.api.ISided;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

public class PocketAttackBlockCallbackListener implements ISided.AttackBlockCallback {
    @Override
    public InteractionResult attack(Player player, InteractionHand hand, BlockPos pos, Direction direction) {
        var level = player.level();
        return PocketListenerUtil.getAddon(PocketAddon.PocketAddonType.PREVENT_BLOCK_MODIFICATION_ADDON, level, pos)
                .map(addon -> addon.attackBlock(player, hand, pos, direction))
                .filter(result -> result != InteractionResult.PASS)
                .orElse(InteractionResult.PASS);
    }
}
