package org.dimdev.dimdoors.listener.pocket;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.dimdev.limlib.api.ISided;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

public class UseItemCallbackListener implements ISided.UseItemCallback {
    @Override
    public InteractionResult use(Player player, InteractionHand hand) {
        var world = player.level();
        return PocketListenerUtil.getAddon(PocketAddon.PocketAddonType.PREVENT_BLOCK_MODIFICATION_ADDON, world, player.blockPosition())
                .map(addon -> addon.useItem(player, hand))
                .filter(result -> result != InteractionResult.PASS)
                .orElse(InteractionResult.PASS);
    }
}
