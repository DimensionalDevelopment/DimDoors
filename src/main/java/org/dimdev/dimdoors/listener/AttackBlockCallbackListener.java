package org.dimdev.dimdoors.listener;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.dimdev.dimdoors.item.ModItem;
import org.dimdev.dimdoors.network.ClientPacketHandler;
import org.dimdev.dimdoors.network.c2s.HitBlockWithItemC2SPacket;

public class AttackBlockCallbackListener implements AttackBlockCallback {

	@Override
	public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
		if (!world.isClient) return ActionResult.PASS;
		Item item = player.getStackInHand(hand).getItem();
		if (!(item instanceof ModItem)) {
			return ActionResult.PASS;
		}
		TypedActionResult<Boolean> result = ((ModItem) item).onAttackBlock(world, player, hand, pos, direction);
		if (result.getValue()) {
			if (!ClientPacketHandler.sendPacket(new HitBlockWithItemC2SPacket(hand, pos, direction))) {
				return ActionResult.FAIL;
			}
		}

		return result.getResult();
	}
}
