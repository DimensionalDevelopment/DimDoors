package org.dimdev.dimdoors.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sounds.SoundSource;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.World;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RotatedLocation;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.rift.targets.RiftReference;
import org.dimdev.dimdoors.sound.ModSoundEvents;

public class StabilizedRiftSignatureItem extends Item { // TODO: common superclass with rift signature
	public static final String ID = "stabilized_rift_signature";

	public StabilizedRiftSignatureItem(Item.Properties settings) {
		super(settings);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return stack.getTag() != null && stack.getTag().contains("destination");
	}

	@Override
	public InteractionResult useOn(UseOnContext itemUsageContext) {
		Player player = itemUsageContext.getPlayer();
		Level world = itemUsageContext.getLevel();
		BlockPos pos = itemUsageContext.getClickedPos()();
		InteractionHand hand = itemUsageContext.getHand();
		Direction side = itemUsageContext.getClickedFace();

		BlockPlaceContext itemPlacementContext = new BlockPlaceContext(itemUsageContext);

		ItemStack stack = player.getItemInHand(hand);
		pos = world.getBlockState(pos).getBlock().canBeReplaced(world.getBlockState(pos), new BlockPlaceContext(itemUsageContext)) ? pos : pos.relative(side);
		// Fail if the player can't place a block there
		if (!player.mayUseItemAt(pos, side.getOpposite(), stack)) {
			return InteractionResult.FAIL;
		}

		if (world.isClientSide) {
			return InteractionResult.SUCCESS;
		}

		RotatedLocation target = getTarget(stack);

		if (target == null) {
			// The link signature has not been used. Store its current target as the first location.
			setSource(stack, new RotatedLocation(world.dimension(), pos, player.getYRot(), 0));
			player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".stored"), true);
			world.playSound(null, player.blockPosition(), ModSoundEvents.RIFT_START.get(), SoundSource.BLOCKS, 0.6f, 1);
		} else {
			// Place a rift at the target point
			if (target.getBlockState().getBlock() != ModBlocks.DETACHED_RIFT) {
				if (!target.getBlockState().getBlock().canBeReplaced(world.getBlockState(target.getBlockPos()), itemPlacementContext)) {
					player.displayClientMessage(Component.translatable("tools.target_became_block"), true);
					// Don't clear source, stabilized signatures always stay bound
					return InteractionResult.FAIL;
				}
				Level targetWorld = DimensionalDoors.getWorld(target.world);
				targetWorld.setBlockAndUpdate(target.getBlockPos(), ModBlocks.DETACHED_RIFT.get().defaultBlockState());
				DetachedRiftBlockEntity rift1 = (DetachedRiftBlockEntity) target.getBlockEntity();
				rift1.register();
			}

			// Place a rift at the source point
			world.setBlockAndUpdate(pos, ModBlocks.DETACHED_RIFT.get().defaultBlockState());
			DetachedRiftBlockEntity rift2 = (DetachedRiftBlockEntity) world.getBlockEntity(pos);
			rift2.setDestination(RiftReference.tryMakeRelative(new Location((ServerLevel) world, pos), target));
			rift2.register();

			stack.hurtAndBreak(1, player, playerEntity -> {});

			player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".created"), true);
			world.playSound(null, player.blockPosition(), ModSoundEvents.RIFT_END.get(), SoundSource.BLOCKS, 0.6f, 1);
		}

		return InteractionResult.SUCCESS;
	}

	public static void setSource(ItemStack itemStack, RotatedLocation destination) {
		if (!itemStack.hasTag()) itemStack.setTag(new CompoundTag());
		itemStack.getTag().put("destination", RotatedLocation.serialize(destination));
	}

	public static void clearSource(ItemStack itemStack) {
		if (itemStack.hasTag()) {
			itemStack.getTag().remove("destination");
		}
	}

	public static RotatedLocation getTarget(ItemStack itemStack) {
		if (itemStack.hasTag() && itemStack.getTag().contains("destination")) {
			return RotatedLocation.deserialize(itemStack.getTag().getCompound("destination"));
		} else {
			return null;
		}
	}
}
