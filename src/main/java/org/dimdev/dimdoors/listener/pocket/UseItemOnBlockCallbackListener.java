package org.dimdev.dimdoors.listener.pocket;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback;

import java.util.List;

public class UseItemOnBlockCallbackListener implements UseItemOnBlockCallback {
	@Override
	public ActionResult useItemOnBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
		List<UseItemOnBlockCallback> applicableAddons;
		if (world.isClient) applicableAddons = PocketListenerUtil.applicableAddonsClient(UseItemOnBlockCallback.class, world, player.getBlockPos());
		else applicableAddons = PocketListenerUtil.applicableAddons(UseItemOnBlockCallback.class, world, player.getBlockPos());

		for (UseItemOnBlockCallback listener : applicableAddons) {
			ActionResult result = listener.useItemOnBlock(player, world, hand, hitResult);
			if (result != ActionResult.PASS) return result;
		}
		return ActionResult.PASS;
	}
}
