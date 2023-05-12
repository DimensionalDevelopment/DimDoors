package org.dimdev.dimdoors.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RotatedLocation;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.client.ToolTipHelper;
import org.dimdev.dimdoors.rift.targets.RiftReference;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.ModDimensions;

import java.util.List;

public class RiftSignatureItem extends Item {
	public static final String ID = "rift_signature";

	public RiftSignatureItem(Item.Properties settings) {
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
		BlockPos pos = itemUsageContext.getClickedPos();
		InteractionHand hand = itemUsageContext.getHand();
		Direction side = itemUsageContext.getClickedFace();

		BlockPlaceContext placementContext = new BlockPlaceContext(itemUsageContext);

		ItemStack stack = player.getItemInHand(hand);
		pos = world.getBlockState(pos).getBlock().canBeReplaced(world.getBlockState(pos), placementContext) ? pos : pos.relative(side);

		// Fail if the player can't place a block there
		if (!player.mayUseItemAt(pos, side.getOpposite(), stack)) {
			return InteractionResult.FAIL;
		}

		if (world.isClientSide) {
			return InteractionResult.SUCCESS;
		}

		if(ModDimensions.isPrivatePocketDimension(world) && !DimensionalDoors.getConfig().getPocketsConfig().canUseRiftSignatureInPrivatePockets) {
			player.displayClientMessage(Component.translatable("tools.signature_blocked").withStyle(ChatFormatting.BLACK), true);
			return InteractionResult.FAIL;
		}

		RotatedLocation target = getSource(stack);

		if (target == null) {
			// The link signature has not been used. Store its current target as the first location.
			setSource(stack, new RotatedLocation(world.dimension(), pos, player.getYRot(), 0));
			player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".stored"), true);
			world.playSound(null, player.blockPosition(), ModSoundEvents.RIFT_START.get(), SoundSource.BLOCKS, 0.6f, 1);
		} else {
			// Place a rift at the saved point
			if (target.getBlockState().getBlock() != ModBlocks.DETACHED_RIFT) {
				if (!target.getBlockState().getBlock().isPossibleToRespawnInThis()) {
					player.displayClientMessage(Component.translatable("tools.target_became_block"), true);
					clearSource(stack); // TODO: But is this fair? It's a rather hidden way of unbinding your signature!
					return InteractionResult.FAIL;
				}
				Level sourceWorld = DimensionalDoors.getWorld(target.world);
				sourceWorld.setBlockAndUpdate(target.getBlockPos(), ModBlocks.DETACHED_RIFT.get().defaultBlockState());
				DetachedRiftBlockEntity rift1 = (DetachedRiftBlockEntity) target.getBlockEntity();
				rift1.setDestination(RiftReference.tryMakeRelative(target, new Location((ServerLevel) world, pos)));
				rift1.register();
			}

			// Place a rift at the target point
			world.setBlockAndUpdate(pos, ModBlocks.DETACHED_RIFT.get().defaultBlockState());
			DetachedRiftBlockEntity rift2 = (DetachedRiftBlockEntity) world.getBlockEntity(pos);
			rift2.setDestination(RiftReference.tryMakeRelative(new Location((ServerLevel) world, pos), target));
			rift2.register();

			stack.hurtAndBreak(1, player, a -> {}); // TODO: calculate damage based on position?

			clearSource(stack);
			player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".created"), true);
			// null = send sound to the player too, we have to do this because this code is not run client-side
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

	public static RotatedLocation getSource(ItemStack itemStack) {
		if (itemStack.hasTag() && itemStack.getTag().contains("destination")) {
			return RotatedLocation.deserialize(itemStack.getTag().getCompound("destination"));
		} else {
			return null;
		}
	}

	@Override
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> list, TooltipFlag tooltipContext) {
		RotatedLocation transform = getSource(itemStack);
		if (transform != null) {
			list.add(Component.translatable(this.getDescriptionId() + ".bound.info0", transform.getX(), transform.getY(), transform.getZ(), transform.getWorldId().location()));
			list.add(Component.translatable(this.getDescriptionId() + ".bound.info1", transform.getWorldId().location()));
		} else {
			ToolTipHelper.processTranslation(list, this.getDescriptionId() + ".unbound.info");
		}
	}
}
