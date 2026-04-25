package org.dimdev.dimdoors.listener.pocket;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.network.client.ClientPacketListener;

import java.util.List;

public class UseItemCallbackListener implements InteractionEvent.RightClickItem {
    @Override
    public CompoundEventResult<ItemStack> click(Player player, InteractionHand hand) {
//        TODO: Implment Right click controlling addon
//    List<InteractionEvent.RightClickItem> applicableAddons;
//    var world = player.level();
//    if (world.isClientSide) applicableAddons = ClientPacketListener.getAddonClient(InteractionEvent.RightClickItem.class, world, player.blockPosition());
//    else applicableAddons = PocketListenerUtil.getAddonCommon(InteractionEvent.RightClickItem.class, world, player.blockPosition());
//
//    for (InteractionEvent.RightClickItem listener : applicableAddons) {
//        CompoundEventResult<ItemStack> result = listener.click(player, hand);
//        if (result.result() != EventResult.pass()) return result;
//    }
    return CompoundEventResult.pass();
    }
}
