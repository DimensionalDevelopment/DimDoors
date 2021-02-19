package org.dimdev.dimdoors.listener.pocket;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class PocketAttackBlockCallbackListener implements AttackBlockCallback {
	@Override
	public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
		List<AttackBlockCallback> applicableAddons;
		if (world.isClient) applicableAddons = PocketListenerUtil.applicableAddonsClient(AttackBlockCallback.class, world, pos);
		else applicableAddons = PocketListenerUtil.applicableAddons(AttackBlockCallback.class, world, pos);

		ActionResult result;
		for (AttackBlockCallback listener : applicableAddons) {
			result = listener.interact(player, world, hand, pos, direction);
			if (result != ActionResult.PASS) return result;
		}
		return ActionResult.PASS;
	}
}
