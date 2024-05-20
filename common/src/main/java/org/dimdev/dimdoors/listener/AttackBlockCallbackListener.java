package org.dimdev.dimdoors.listener;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.dimdev.dimdoors.api.item.ExtendedItem;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;
import org.dimdev.dimdoors.network.packet.c2s.HitBlockWithItemC2SPacket;

public class AttackBlockCallbackListener implements InteractionEvent.LeftClickBlock {
	@Override
	public EventResult click(Player player, InteractionHand hand, BlockPos pos, Direction direction) {
		var world = player.level;

		if (!world.isClientSide) return EventResult.pass();
		Item item = player.getItemInHand(hand).getItem();
		if (!(item instanceof ExtendedItem)) {
			return EventResult.pass();
		}

		CompoundEventResult<Boolean> result = ((ExtendedItem) item).onAttackBlock(world, player, hand, pos, direction);
		if (result.object()) {
			if (!ClientPacketHandler.sendPacket(new HitBlockWithItemC2SPacket(hand, pos, direction))) {
				return EventResult.interruptFalse();
			}
		}

		return result.result();
	}
}
