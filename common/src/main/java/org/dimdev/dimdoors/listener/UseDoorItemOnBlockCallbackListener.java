package org.dimdev.dimdoors.listener;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar;

public class UseDoorItemOnBlockCallbackListener implements UseItemOnBlockCallback {
	@Override
	public InteractionResult useItemOnBlock(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
		if (world.getBlockState(hitResult.getBlockPos()).getBlock() != ModBlocks.DETACHED_RIFT.get()) return InteractionResult.PASS;
		ItemStack stack = player.getItemInHand(hand);
		DimensionalDoorItemRegistrar registrar = DimensionalDoors.getDimensionalDoorItemRegistrar();
		Item item = stack.getItem();
		if (registrar.isRegistered(item)) {
			return registrar.place(item, new BlockPlaceContext(player, hand, stack, hitResult));
		}
		return InteractionResult.PASS;
	}
}
