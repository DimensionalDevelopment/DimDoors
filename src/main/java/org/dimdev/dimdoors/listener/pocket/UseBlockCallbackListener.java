package org.dimdev.dimdoors.listener.pocket;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import java.util.List;

public class UseBlockCallbackListener implements UseBlockCallback {
	@Override
	public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
		List<UseBlockCallback> applicableAddons;
		if (world.isClient) applicableAddons = PocketListenerUtil.applicableAddonsClient(UseBlockCallback.class, world, player.getBlockPos());
		else applicableAddons = PocketListenerUtil.applicableAddons(UseBlockCallback.class, world, player.getBlockPos());

		for (UseBlockCallback listener : applicableAddons) {
			ActionResult result = listener.interact(player, world, hand, hitResult);
			if (result != ActionResult.PASS) return result;
		}
		return ActionResult.PASS;
	}
}
