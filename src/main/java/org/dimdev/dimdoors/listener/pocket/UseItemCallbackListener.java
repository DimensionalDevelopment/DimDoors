package org.dimdev.dimdoors.listener.pocket;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class UseItemCallbackListener implements UseItemCallback {
	@Override
	public TypedActionResult<ItemStack> interact(PlayerEntity player, World world, Hand hand) {
		List<UseItemCallback> applicableAddons;
		if (world.isClient) applicableAddons = PocketListenerUtil.applicableAddonsClient(UseItemCallback.class, world, player.getBlockPos());
		else applicableAddons = PocketListenerUtil.applicableAddons(UseItemCallback.class, world, player.getBlockPos());

		for (UseItemCallback listener : applicableAddons) {
			TypedActionResult<ItemStack> result = listener.interact(player, world, hand);
			if (result.getResult() != ActionResult.PASS) return result;
		}
		return TypedActionResult.pass(player.getStackInHand(hand));
	}
}
