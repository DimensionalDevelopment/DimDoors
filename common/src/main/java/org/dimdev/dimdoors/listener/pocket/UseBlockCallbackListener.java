package org.dimdev.dimdoors.listener.pocket;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.dimdev.dimcore.api.ISided;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

public class UseBlockCallbackListener implements ISided.UseBlockCallback {
    @Override
    public InteractionResult use(Player player, InteractionHand hand, BlockHitResult hitResult) {
        var world = player.level();
        return PocketListenerUtil.getAddon(PocketAddon.PocketAddonType.PREVENT_BLOCK_MODIFICATION_ADDON, world, hitResult.getBlockPos())
                .map(addon -> addon.useBlock(player, world, hand, hitResult))
                .filter(result -> result != InteractionResult.PASS)
                .orElse(InteractionResult.PASS);
    }
}
