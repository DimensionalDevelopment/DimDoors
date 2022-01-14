package org.dimdev.dimdoors.listener;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.DimensionalDoorItemRegistrar;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class UseDoorItemOnBlockCallbackListener implements UseItemOnBlockCallback {
	@Override
	public ActionResult useItemOnBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
		if (world.getBlockState(hitResult.getBlockPos()).getBlock() != ModBlocks.DETACHED_RIFT) return ActionResult.PASS;
		ItemStack stack = player.getStackInHand(hand);
		DimensionalDoorItemRegistrar registrar = DimensionalDoorsInitializer.getDimensionalDoorItemRegistrar();
		Item item = stack.getItem();
		if (registrar.isRegistered(item)) {
			return registrar.place(item, new ItemPlacementContext(player, hand, stack, hitResult));
		}
		return ActionResult.PASS;
	}
}
