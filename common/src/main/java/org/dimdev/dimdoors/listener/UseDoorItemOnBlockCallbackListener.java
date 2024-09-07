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
import org.dimdev.dimdoors.item.RaycastHelper;
import org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar;
import org.jetbrains.annotations.Nullable;

import static org.dimdev.dimdoors.item.RaycastHelper.DETACH;

public class UseDoorItemOnBlockCallbackListener implements UseItemOnBlockCallback {
	@Override
	public InteractionResult useItemOnBlock(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
		var result = RaycastHelper.findDetachRift(player, DETACH);

		if (!RaycastHelper.hitsDetachedRift(result, world)) return InteractionResult.PASS;
		ItemStack stack = player.getItemInHand(hand);
		DimensionalDoorItemRegistrar registrar = DimensionalDoors.getDimensionalDoorItemRegistrar();
		Item item = stack.getItem();
		if (registrar.isRegistered(item)) {
			return registrar.place(item, new DimDoorBlockPlaceContext(player, hand, stack, (BlockHitResult) result));
		}

		return InteractionResult.PASS; //item instanceof RiftRemoverItem || (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof DimensionalDoorBlock) ? InteractionResult.PASS : InteractionResult.FAIL;
	}

	public static class DimDoorBlockPlaceContext extends BlockPlaceContext {

		public DimDoorBlockPlaceContext(@Nullable Player player, InteractionHand hand, ItemStack itemStack, BlockHitResult hitResult) {
			super(player, hand, itemStack, hitResult);
			this.replaceClicked = true;
		}

		public DimDoorBlockPlaceContext(BlockPlaceContext context, BlockHitResult result) {
			this(context.getPlayer(), context.getHand(), context.getItemInHand(), result);
		}

		public void setToProperReplaced() {
			this.replaceClicked = getLevel().getBlockState(getHitResult().getBlockPos()).canBeReplaced(this);
		}
	}
}
