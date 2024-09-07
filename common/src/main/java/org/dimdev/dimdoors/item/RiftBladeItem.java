package org.dimdev.dimdoors.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.DimensionalPortalBlock;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.client.ToolTipHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static org.dimdev.dimdoors.item.RaycastHelper.DETACH;

public class RiftBladeItem extends SwordItem {
	public static final String ID = "rift_blade";

	public RiftBladeItem(Item.Properties settings) {
		super(Tiers.IRON, 3, -2.4F, settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		ToolTipHelper.processTranslation(list, this.getDescriptionId() + ".info");
	}

	@Override
	public boolean isFoil(ItemStack itemStack) {
		return true;
	}

	@Override
	public boolean isValidRepairItem(ItemStack item, ItemStack repairingItem) {
		return Objects.equals(ModItems.STABLE_FABRIC.get(), repairingItem.getItem());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		HitResult hit = RaycastHelper.raycast(player, 0.0F, LivingEntity.class::isInstance);

		if (hit == null || hit.getType() == HitResult.Type.MISS) {
			hit = RaycastHelper.raycast(player, 1.0F, LivingEntity.class::isInstance);
		}

		if (hit == null) {
			hit = RaycastHelper.findDetachRift(player, DETACH);
		}
//
//		if (hit == null) {
//			hit = player.pick(16, 0, false);
//		}

		if (world.isClientSide) {
			if (RaycastHelper.hitsLivingEntity(hit) || RaycastHelper.hitsRift(hit, world)) {
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
			} else {
				player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".rift_miss"), true);
				RiftBlockEntity.showRiftCoreUntil = System.currentTimeMillis() + DimensionalDoors.getConfig().getGraphicsConfig().highlightRiftCoreFor;
				return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
			}
		}

		if (RaycastHelper.hitsLivingEntity(hit)) {
//			double damageMultiplier = (double) stack.getDamageValue() / (double) stack.getMaxDamage(); //TODO: Decide if to remove old code or still use.
//			// TODO: gaussian, instead or random
//			double offsetDistance = Math.random() * damageMultiplier * 7 + 2; //TODO: make these offset distances configurable
//			double offsetRotationYaw = (Math.random() - 0.5) * damageMultiplier * 360;
//
//			var playerVec = player.position();
//			var entityVec = hit.getLocation();
//			var offsetDirection = playerVec.subtract(entityVec).normalize();
//			offsetDirection = offsetDirection.yRot((float) (offsetRotationYaw * Math.PI) / 180);
//
//			Vec3 added = entityVec.add(offsetDirection.scale(offsetDistance));
//			BlockPos teleportPosition = new BlockPos(new Vec3i((int) added.x, (int) added. y, (int) added.z));
//			while (world.getBlockState(teleportPosition).blocksMotion())
//				teleportPosition = teleportPosition.above();
//			player.teleportTo(teleportPosition.getX(), teleportPosition.getY(), teleportPosition.getZ());
//			player.setYRot((float) (Math.random() * 2 * Math.PI));
//
//			stack.hurtAndBreak(1, player, a -> a.broadcastBreakEvent(hand));



			// Determine target position directly from the hit location
			Vec3 targetVec = hit.getLocation();

			BlockPos teleportPosition = new BlockPos((int) targetVec.x(), (int) targetVec.y(), (int) targetVec.z());

			// Ensure the target position is not inside a block
			while (!world.getBlockState(teleportPosition).isAir() && !world.getBlockState(teleportPosition).getFluidState().isEmpty()) {
				teleportPosition = teleportPosition.above();
			}

			world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);

			// Teleport the player to the target position
			player.teleportTo(teleportPosition.getX() + 0.5, teleportPosition.getY(), teleportPosition.getZ() + 0.5);

			// Calculate and set the yaw rotation to face the target entity
			Vec3 direction = targetVec.subtract(player.position()).normalize();
			float yaw = (float) (Math.atan2(direction.z, direction.x) * (180 / Math.PI)) - 90;
			player.setYRot(yaw);



			// Apply damage to the item stack
			stack.hurtAndBreak(1, player, a -> a.broadcastBreakEvent(hand));

			return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
		} else if (RaycastHelper.hitsDetachedRift(hit, world)) {
			BlockHitResult blockHitResult = (BlockHitResult) hit;
			BlockPos pos = blockHitResult.getBlockPos();
			RiftBlockEntity rift = (RiftBlockEntity) world.getBlockEntity(blockHitResult.getBlockPos());

			world.setBlockAndUpdate(pos, ModBlocks.DIMENSIONAL_PORTAL.get().defaultBlockState().setValue(DimensionalPortalBlock.FACING, blockHitResult.getDirection().getOpposite()));
			((EntranceRiftBlockEntity) world.getBlockEntity(pos)).setData(rift.getData());

			stack.hurtAndBreak(1, player, a -> a.broadcastBreakEvent(hand));
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
		}
		return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
	}
}
