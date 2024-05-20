package org.dimdev.dimdoors.listener.pocket;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class UseItemCallbackListener implements InteractionEvent.RightClickItem {
	@Override
	public CompoundEventResult<ItemStack> click(Player player, InteractionHand hand) {
		List<InteractionEvent.RightClickItem> applicableAddons;
		var world = player.level;
		if (world.isClientSide) applicableAddons = PocketListenerUtil.applicableAddonsClient(InteractionEvent.RightClickItem.class, world, player.blockPosition());
		else applicableAddons = PocketListenerUtil.applicableAddonsCommon(InteractionEvent.RightClickItem.class, world, player.blockPosition());

		for (InteractionEvent.RightClickItem listener : applicableAddons) {
			CompoundEventResult<ItemStack> result = listener.click(player, hand);
			if (result.result() != EventResult.pass()) return result;
		}
		return CompoundEventResult.pass();
	}
}
