package org.dimdev.dimdoors.listener.pocket;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

public class UseItemOnBlockCallbackListener implements UseItemOnBlockCallback {
    @Override
    public InteractionResult useItemOnBlock(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
    return PocketListenerUtil.getAddon(PocketAddon.PocketAddonType.PREVENT_BLOCK_MODIFICATION_ADDON.get(), world, player.blockPosition()).map(addon -> addon.useItemOnBlock(player, world, hand, hitResult)).filter(result -> result != InteractionResult.PASS).orElse(InteractionResult.PASS);
    }
}
