package org.dimdev.dimdoors.listener.pocket;

import java.util.List;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class UseItemCallbackListener implements UseItemCallback {
	@Override
	public InteractionResultHolder<ItemStack> interact(Player player, Level world, InteractionHand hand) {
		List<UseItemCallback> applicableAddons;
		if (world.isClientSide) applicableAddons = PocketListenerUtil.applicableAddonsClient(UseItemCallback.class, world, player.blockPosition());
		else applicableAddons = PocketListenerUtil.applicableAddons(UseItemCallback.class, world, player.blockPosition());

		for (UseItemCallback listener : applicableAddons) {
			InteractionResultHolder<ItemStack> result = listener.interact(player, world, hand);
			if (result.getResult() != InteractionResult.PASS) return result;
		}
		return InteractionResultHolder.pass(player.getItemInHand(hand));
	}
}
