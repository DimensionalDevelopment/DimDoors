package org.dimdev.dimdoors.item;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.client.ToolTipHelper;
import org.dimdev.dimdoors.rift.targets.RiftReference;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RotatedLocation;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class StabilizedRiftSignatureItem extends Item { // TODO: common superclass with rift signature
	public static final String ID = "stabilized_rift_signature";

	public StabilizedRiftSignatureItem(Settings settings) {
		super(settings);
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return stack.getNbt() != null && stack.getNbt().contains("destination");
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		PlayerEntity player = itemUsageContext.getPlayer();
		World world = itemUsageContext.getWorld();
		BlockPos pos = itemUsageContext.getBlockPos();
		Hand hand = itemUsageContext.getHand();
		Direction side = itemUsageContext.getSide();

		ItemPlacementContext itemPlacementContext = new ItemPlacementContext(itemUsageContext);

		ItemStack stack = player.getStackInHand(hand);
		pos = world.getBlockState(pos).getBlock().canReplace(world.getBlockState(pos), new ItemPlacementContext(itemUsageContext)) ? pos : pos.offset(side);
		// Fail if the player can't place a block there
		if (!player.canPlaceOn(pos, side.getOpposite(), stack)) {
			return ActionResult.FAIL;
		}

		if (world.isClient) {
			return ActionResult.SUCCESS;
		}

		RotatedLocation target = getTarget(stack);

		if (target == null) {
			// The link signature has not been used. Store its current target as the first location.
			setSource(stack, new RotatedLocation(world.getRegistryKey(), pos, player.getYaw(), 0));
			player.sendMessage(new TranslatableText(this.getTranslationKey() + ".stored"), true);
			world.playSound(null, player.getBlockPos(), ModSoundEvents.RIFT_START, SoundCategory.BLOCKS, 0.6f, 1);
		} else {
			// Place a rift at the target point
			if (target.getBlockState().getBlock() != ModBlocks.DETACHED_RIFT) {
				if (!target.getBlockState().getBlock().canReplace(world.getBlockState(target.getBlockPos()), itemPlacementContext)) {
					player.sendMessage(new TranslatableText("tools.target_became_block"), true);
					// Don't clear source, stabilized signatures always stay bound
					return ActionResult.FAIL;
				}
				World targetWorld = DimensionalDoorsInitializer.getWorld(target.world);
				targetWorld.setBlockState(target.getBlockPos(), ModBlocks.DETACHED_RIFT.getDefaultState());
				DetachedRiftBlockEntity rift1 = (DetachedRiftBlockEntity) target.getBlockEntity();
				rift1.register();
			}

			// Place a rift at the source point
			world.setBlockState(pos, ModBlocks.DETACHED_RIFT.getDefaultState());
			DetachedRiftBlockEntity rift2 = (DetachedRiftBlockEntity) world.getBlockEntity(pos);
			rift2.setDestination(RiftReference.tryMakeRelative(new Location((ServerWorld) world, pos), target));
			rift2.register();

			stack.damage(1, player, playerEntity -> {
			});

			player.sendMessage(new TranslatableText(this.getTranslationKey() + ".created"), true);
			world.playSound(null, player.getBlockPos(), ModSoundEvents.RIFT_END, SoundCategory.BLOCKS, 0.6f, 1);
		}

		return ActionResult.SUCCESS;
	}

	public static void setSource(ItemStack itemStack, RotatedLocation destination) {
		if (!itemStack.hasNbt()) itemStack.setNbt(new NbtCompound());
		itemStack.getNbt().put("destination", RotatedLocation.serialize(destination));
	}

	public static void clearSource(ItemStack itemStack) {
		if (itemStack.hasNbt()) {
			itemStack.getNbt().remove("destination");
		}
	}

	public static RotatedLocation getTarget(ItemStack itemStack) {
		if (itemStack.hasNbt() && itemStack.getNbt().contains("destination")) {
			return RotatedLocation.deserialize(itemStack.getNbt().getCompound("destination"));
		} else {
			return null;
		}
	}
}
