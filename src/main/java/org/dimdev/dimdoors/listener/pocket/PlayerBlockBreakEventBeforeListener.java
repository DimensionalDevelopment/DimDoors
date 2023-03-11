package org.dimdev.dimdoors.listener.pocket;

import java.util.List;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PlayerBlockBreakEventBeforeListener implements PlayerBlockBreakEvents.Before {
	@Override
	public boolean beforeBlockBreak(Level world, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		List<PlayerBlockBreakEvents.Before> applicableAddons;
		if (world.isClientSide) applicableAddons = PocketListenerUtil.applicableAddonsClient(PlayerBlockBreakEvents.Before.class, world, player.blockPosition());
		else applicableAddons = PocketListenerUtil.applicableAddons(PlayerBlockBreakEvents.Before.class, world, player.blockPosition());

		for (PlayerBlockBreakEvents.Before listener : applicableAddons) {
			if (!listener.beforeBlockBreak(world, player, pos, state, blockEntity)) return false;
		}
		return true;
	}
}
