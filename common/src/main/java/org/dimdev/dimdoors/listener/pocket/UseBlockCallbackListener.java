package org.dimdev.dimdoors.listener.pocket;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.dimdev.dimdoors.network.client.ClientPacketListener;

import java.util.List;

public class UseBlockCallbackListener implements InteractionEvent.RightClickBlock {
	@Override
	public EventResult click(Player player, InteractionHand hand, BlockPos pos, Direction face) {
//        TODO: Implment right click addon
//
//		var world = player.level();
//        List<InteractionEvent.RightClickBlock> applicableAddons = PocketListenerUtil.applicableAddons(InteractionEvent.RightClickBlock.class, world, pos);
//
//		for (InteractionEvent.RightClickBlock listener : applicableAddons) {
//			EventResult result = listener.click(player, hand, pos, face);
//			if (result != EventResult.pass()) return result;
//		}
		return EventResult.pass();
	}
}
