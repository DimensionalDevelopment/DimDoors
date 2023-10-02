package org.dimdev.dimdoors.listener.pocket;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class UseBlockCallbackListener implements InteractionEvent.RightClickBlock {
	@Override
	public EventResult click(Player player, InteractionHand hand, BlockPos pos, Direction face) {
		List<InteractionEvent.RightClickBlock> applicableAddons;
		var world = player.level();
		if (world.isClientSide) applicableAddons = PocketListenerUtil.applicableAddonsClient(InteractionEvent.RightClickBlock.class, world, pos);
		else applicableAddons = PocketListenerUtil.applicableAddons(InteractionEvent.RightClickBlock.class, world, pos);

		for (InteractionEvent.RightClickBlock listener : applicableAddons) {
			EventResult result = listener.click(player, hand, pos, face);
			if (result != EventResult.pass()) return result;
		}
		return EventResult.pass();
	}
}
