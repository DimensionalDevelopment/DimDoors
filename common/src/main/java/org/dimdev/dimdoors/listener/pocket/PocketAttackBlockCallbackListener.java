package org.dimdev.dimdoors.listener.pocket;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

public class PocketAttackBlockCallbackListener implements InteractionEvent.LeftClickBlock {
    @Override
    public EventResult click(Player player, InteractionHand hand, BlockPos pos, Direction direction) {

    var level = player.level();
        return PocketListenerUtil.getAddon(PocketAddon.PocketAddonType.PREVENT_BLOCK_MODIFICATION_ADDON.get(), level, pos).map(addon -> addon.click(player, hand, pos, direction)).filter(result -> result != EventResult.pass()).orElse(EventResult.pass());
    }
}
