package org.dimdev.dimdoors.listener.pocket;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class PlayerBlockBreakEventBeforeListener implements PlayerBlockBreakEvents.Before {
	@Override
	public boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		List<PlayerBlockBreakEvents.Before> applicableAddons;
		if (world.isClient) applicableAddons = PocketListenerUtil.applicableAddonsClient(PlayerBlockBreakEvents.Before.class, world, player.getBlockPos());
		else applicableAddons = PocketListenerUtil.applicableAddons(PlayerBlockBreakEvents.Before.class, world, player.getBlockPos());

		for (PlayerBlockBreakEvents.Before listener : applicableAddons) {
			if (!listener.beforeBlockBreak(world, player, pos, state, blockEntity)) return false;
		}
		return true;
	}
}
