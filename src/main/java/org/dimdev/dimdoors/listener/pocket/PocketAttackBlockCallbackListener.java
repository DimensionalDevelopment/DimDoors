package org.dimdev.dimdoors.listener.pocket;

import java.util.List;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PocketAttackBlockCallbackListener implements AttackBlockCallback {
	@Override
	public InteractionResult interact(Player player, Level world, InteractionHand hand, BlockPos pos, Direction direction) {
		List<AttackBlockCallback> applicableAddons;
		if (world.isClientSide) applicableAddons = PocketListenerUtil.applicableAddonsClient(AttackBlockCallback.class, world, pos);
		else applicableAddons = PocketListenerUtil.applicableAddons(AttackBlockCallback.class, world, pos);

		InteractionResult result;
		for (AttackBlockCallback listener : applicableAddons) {
			result = listener.interact(player, world, hand, pos, direction);
			if (result != InteractionResult.PASS) return result;
		}
		return InteractionResult.PASS;
	}
}
