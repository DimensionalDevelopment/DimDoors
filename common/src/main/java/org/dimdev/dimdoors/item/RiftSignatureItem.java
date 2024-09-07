package org.dimdev.dimdoors.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
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
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.RotatedLocation;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.client.ToolTipHelper;
import org.dimdev.dimdoors.rift.targets.RiftReference;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.ModDimensions;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class RiftSignatureItem extends Item {
	public static final String ID = "rift_signature";
	public boolean shouldclear;

	public RiftSignatureItem(Item.Properties settings, boolean clear) {

		super(settings);
		shouldclear = clear;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return stack.getTag() != null && stack.getTag().contains("destination");
	}

	@Override
	public InteractionResult useOn(UseOnContext itemUsageContext) {
		Player player = itemUsageContext.getPlayer();
		Level world = itemUsageContext.getLevel();
		// get block one block above the clicked block
		BlockPos pos = itemUsageContext.getClickedPos();
		InteractionHand hand = itemUsageContext.getHand();
		BlockState state = world.getBlockState(pos);
		Direction side = itemUsageContext.getClickedFace();

		var placement = new BlockPlaceContext(itemUsageContext);

		ItemStack stack = player.getItemInHand(hand);
		var placedRiftLogic = getLogic(world, pos, itemUsageContext.getItemInHand());

		if (placedRiftLogic == null) {
			pos = pos.relative(side);
			placedRiftLogic = getLogic(world, pos, itemUsageContext.getItemInHand());
		}

//		if(placedRiftLogic == null) {
//			placedRiftLogic = PlacementLogic.getLogic(world, pos);
//			pos = placedRiftLogic != null ? pos : pos.relative(side);
//		}

		// Fail if the player can't place a block there
		if (!player.mayUseItemAt(pos, side.getOpposite(), stack) || placedRiftLogic == null) {
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
			var source = new RotatedLocation(world.dimension(), pos, player.getYRot(), 0);

			// Place a rift at the saved point
//            if (target.getBlockState().getBlock() instanceof RiftProvider<?> provider) {
//				rift1 = provider.getRift(target.getWorld(),target.pos,target.getBlockState());
//            } else {
//                if (!target.getBlockState().getBlock().isPossibleToRespawnInThis(state)) {
//                    player.displayClientMessage(Component.translatable("tools.target_became_block"), true);
//                    clearSource(stack); // TODO: But is this fair? It's a rather hidden way of unbinding your signature!
//                    return InteractionResult.FAIL;
//                }
//
//            }

			if(placedRiftLogic != null) {
				placedRiftLogic.getRift((ServerLevel) world, pos).ifPresent(a -> a.setDestination(RiftReference.tryMakeRelative(source, target)));
			}

			if((placedRiftLogic = getLogic(target.getWorld(), target.pos, ItemStack.EMPTY)) != null) {
				placedRiftLogic.getRift(target.getWorld(), target.pos).ifPresent(a -> a.setDestination(RiftReference.tryMakeRelative(target, source)));
			}




//			if (world.getBlockState(pos).getBlock() instanceof RiftProvider<?> provider) {
//				rift2 = provider.getRift(world, pos, world.getBlockState(pos));
//			}
//			else{
//				world.setBlockAndUpdate(pos, ModBlocks.DETACHED_RIFT.get().defaultBlockState());
//				rift2 = (DetachedRiftBlockEntity) world.getBlockEntity(pos);
//				rift2.register();
//			}
//			rift2.setDestination(RiftReference.tryMakeRelative(new Location((ServerLevel) world, pos), target));

			// Place a rift at the target point


			stack.hurtAndBreak(1, player, a -> {}); // TODO: calculate damage based on position?
			if(shouldclear){
				clearSource(stack);
			}
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

	public PlacementLogic getLogic(Level level, BlockPos pos, ItemStack stack) {
		var state = level.getBlockState(pos);

		if(state.getMaterial().isReplaceable() && (stack.isEmpty() || stack.getItem() != this)) {
			return PlacementLogic.CREATE;
		} else if(state.getBlock() instanceof RiftProvider<?>) {
			return PlacementLogic.EXISTING;
		} else if(state.getBlock() instanceof DoorBlock) {
			return PlacementLogic.DOOR;
		} else {
			return null;
		}
	}

	public enum PlacementLogic {
		CREATE((world, pos) -> {
			world.setBlockAndUpdate(pos, ModBlocks.DETACHED_RIFT.get().defaultBlockState());
			var rift2 = (DetachedRiftBlockEntity) world.getBlockEntity(pos);
			rift2.register();
			return Optional.of(rift2);
		}),
		EXISTING((serverLevel, blockPos) -> {
			var state = serverLevel.getBlockState(blockPos);

			return state.getBlock() instanceof RiftProvider<?> provider ? Optional.ofNullable(provider.getRift(serverLevel, blockPos, state)) : Optional.empty();
		}),
		DOOR((serverLevel, blockPos) -> {
			var state = serverLevel.getBlockState(blockPos);
			if(state.getBlock() instanceof DoorBlock door) {
				if(Registry.BLOCK.get(DimensionalDoors.getDimensionalDoorBlockRegistrar().get(door.arch$registryName())) instanceof DimensionalDoorBlock dimensionalDoorBlock) {
					var dimdoorState = dimensionalDoorBlock.defaultBlockState()
							.setValue(DoorBlock.HINGE, state.getValue(DoorBlock.HINGE))
							.setValue(DoorBlock.FACING, state.getValue(DoorBlock.FACING))
							.setValue(DoorBlock.OPEN, state.getValue(DoorBlock.OPEN))
							.setValue(DoorBlock.POWERED, state.getValue(DoorBlock.OPEN));
					 BlockPos top = state.getValue(DimensionalDoorBlock.HALF) == DoubleBlockHalf.UPPER ? blockPos : blockPos.above();
					 BlockPos bottom = top.below();

					serverLevel.setBlockAndUpdate(top, dimdoorState.setValue(DimensionalDoorBlock.HALF, DoubleBlockHalf.UPPER).setValue(DimensionalDoorBlock.WATERLOGGED, serverLevel.getBlockState(top).getValue(DimensionalDoorBlock.WATERLOGGED)));
					serverLevel.setBlockAndUpdate(bottom, dimdoorState.setValue(DimensionalDoorBlock.HALF, DoubleBlockHalf.LOWER).setValue(DimensionalDoorBlock.WATERLOGGED, serverLevel.getBlockState(bottom).getValue(DimensionalDoorBlock.WATERLOGGED)));
					return Optional.ofNullable(((RiftProvider<?>) serverLevel.getBlockEntity(bottom)).getRift(serverLevel, bottom, serverLevel.getBlockState(bottom)));
				}
			}

			return Optional.empty();
		});

		private BiFunction<ServerLevel, BlockPos, Optional<RiftBlockEntity>> function;

		PlacementLogic(BiFunction<ServerLevel, BlockPos, Optional<RiftBlockEntity>> function) {
			this.function = function;
		}

		public Optional<RiftBlockEntity> getRift(ServerLevel world, BlockPos pos) {
			return function.apply(world, pos);
		}
	}
}
