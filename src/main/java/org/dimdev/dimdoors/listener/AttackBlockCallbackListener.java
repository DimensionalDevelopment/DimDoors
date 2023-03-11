package org.dimdev.dimdoors.listener;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.api.item.ExtendedItem;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;
import org.dimdev.dimdoors.network.packet.c2s.HitBlockWithItemC2SPacket;

public class AttackBlockCallbackListener implements AttackBlockCallback {
	@Override
	public InteractionResult interact(Player player, Level world, InteractionHand hand, BlockPos pos, Direction direction) {
		if (!world.isClientSide) return InteractionResult.PASS;
		Item item = player.getItemInHand(hand).getItem();
		if (!(item instanceof ExtendedItem)) {
			return InteractionResult.PASS;
		}
		InteractionResultHolder<Boolean> result = ((ExtendedItem) item).onAttackBlock(world, player, hand, pos, direction);
		if (result.getObject()) {
			if (!ClientPacketHandler.sendPacket(new HitBlockWithItemC2SPacket(hand, pos, direction))) {
				return InteractionResult.FAIL;
			}
		}

		return result.getResult();
	}
}
