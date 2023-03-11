package org.dimdev.dimdoors.listener.pocket;

import java.util.List;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class UseBlockCallbackListener implements UseBlockCallback {
	@Override
	public InteractionResult interact(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
		List<UseBlockCallback> applicableAddons;
		if (world.isClientSide) applicableAddons = PocketListenerUtil.applicableAddonsClient(UseBlockCallback.class, world, player.blockPosition());
		else applicableAddons = PocketListenerUtil.applicableAddons(UseBlockCallback.class, world, player.blockPosition());

		for (UseBlockCallback listener : applicableAddons) {
			InteractionResult result = listener.interact(player, world, hand, hitResult);
			if (result != InteractionResult.PASS) return result;
		}
		return InteractionResult.PASS;
	}
}
