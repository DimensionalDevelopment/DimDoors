package org.dimdev.dimdoors.listener.pocket;

import java.util.List;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback;

public class UseItemOnBlockCallbackListener implements UseItemOnBlockCallback {
	@Override
	public InteractionResult useItemOnBlock(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
		List<UseItemOnBlockCallback> applicableAddons;
		if (world.isClientSide) applicableAddons = PocketListenerUtil.applicableAddonsClient(UseItemOnBlockCallback.class, world, player.blockPosition());
		else applicableAddons = PocketListenerUtil.applicableAddons(UseItemOnBlockCallback.class, world, player.blockPosition());

		for (UseItemOnBlockCallback listener : applicableAddons) {
			InteractionResult result = listener.useItemOnBlock(player, world, hand, hitResult);
			if (result != InteractionResult.PASS) return result;
		}
		return InteractionResult.PASS;
	}
}
