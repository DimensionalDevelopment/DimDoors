package org.dimdev.dimdoors.listener.pocket;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class PocketAttackBlockCallbackListener implements InteractionEvent.LeftClickBlock {
	@Override
	public EventResult click(Player player, InteractionHand hand, BlockPos pos, Direction direction) {
		List<InteractionEvent.LeftClickBlock> applicableAddons;
		var level = player.getLevel();
		if (level.isClientSide) applicableAddons = PocketListenerUtil.applicableAddonsClient(InteractionEvent.LeftClickBlock.class, level, pos);
		else applicableAddons = PocketListenerUtil.applicableAddons(InteractionEvent.LeftClickBlock.class, level, pos);

		EventResult result;
		for (InteractionEvent.LeftClickBlock listener : applicableAddons) {
			result = listener.click(player, hand, pos, direction);
			if (result != EventResult.pass()) return result;
		}
		return EventResult.pass();
	}
}
