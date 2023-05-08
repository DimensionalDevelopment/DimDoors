package org.dimdev.dimdoors.listener.pocket;

//public class PlayerBlockBreakEventBeforeListener implements PlayerBlockBreakEvents.Before { TODO: Fix
//	@Override
//	public boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
//		List<PlayerBlockBreakEvents.Before> applicableAddons;
//		if (world.isClient) applicableAddons = PocketListenerUtil.applicableAddonsClient(PlayerBlockBreakEvents.Before.class, world, player.getBlockPos());
//		else applicableAddons = PocketListenerUtil.applicableAddons(PlayerBlockBreakEvents.Before.class, world, player.getBlockPos());
//
//		for (PlayerBlockBreakEvents.Before listener : applicableAddons) {
//			if (!listener.beforeBlockBreak(world, player, pos, state, blockEntity)) return false;
//		}
//		return true;
//	}
//}
