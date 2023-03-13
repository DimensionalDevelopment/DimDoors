package org.dimdev.dimdoors.item;

import java.util.List;
import java.util.Objects;
import net.fabricmc.api.Dist;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
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

public class RiftBladeItem extends SwordItem {
	public static final String ID = "rift_blade";

	public RiftBladeItem(Properties settings) {
		super(Tiers.IRON, 3, -2.4F, settings);
	}

	@Environment(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> list, TooltipFlag tooltipContext) {
		ToolTipHelper.processTranslation(list, this.getDescriptionId() + ".info");
	}

	@Override
	public boolean isFoil(ItemStack itemStack) {
		return true;
	}

	@Override
	public boolean isValidRepairItem(ItemStack item, ItemStack repairingItem) {
		return Objects.equals(ModItems.STABLE_FABRIC, repairingItem.getItem());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		HitResult hit = RaycastHelper.raycast(player, 16, 0.0F, LivingEntity.class::isInstance);

		if (hit == null) {
			hit = RaycastHelper.raycast(player, 16, 1.0F, LivingEntity.class::isInstance);
		}

		if (hit == null) {
			hit = player.pick(16, 1.0F, false); //TODO: make the range of the Rift Blade configurable
		}

		if (hit == null) {
			hit = player.pick(16, 0, false);
		}

		if (world.isClientSide) {
			if (RaycastHelper.hitsLivingEntity(hit) || RaycastHelper.hitsRift(hit, world)) {
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
			} else {
				player.displayClientMessage(MutableComponent.create(new TranslatableContents(this.getDescriptionId() + ".rift_miss")), true);
				RiftBlockEntity.showRiftCoreUntil = System.currentTimeMillis() + Constants.CONFIG_MANAGER.get().getGraphicsConfig().highlightRiftCoreFor;
				return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
			}
		}

		if (RaycastHelper.hitsLivingEntity(hit)) {
			double damageMultiplier = (double) stack.getDamageValue() / (double) stack.getMaxDamage();
			// TODO: gaussian, instead or random
			double offsetDistance = Math.random() * damageMultiplier * 7 + 2; //TODO: make these offset distances configurable
			double offsetRotationYaw = (Math.random() - 0.5) * damageMultiplier * 360;

			Vec3 playerVec = player.position();
			Vec3 entityVec = hit.getLocation();
			Vec3 offsetDirection = playerVec.subtract(entityVec).normalize();
			offsetDirection = offsetDirection.yRot((float) (offsetRotationYaw * Math.PI) / 180);

			BlockPos teleportPosition = new BlockPos(entityVec.add(offsetDirection.scale(offsetDistance)));
			while (world.getBlockState(teleportPosition).getMaterial().blocksMotion())
				teleportPosition = teleportPosition.above();
			player.teleportToWithTicket(teleportPosition.getX(), teleportPosition.getY(), teleportPosition.getZ());
			player.setYRot((float) (Math.random() * 2 * Math.PI));

			stack.hurtAndBreak(1, player, a -> a.broadcastBreakEvent(hand));
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
		} else if (RaycastHelper.hitsDetachedRift(hit, world)) {
			BlockHitResult blockHitResult = (BlockHitResult) hit;
			BlockPos pos = blockHitResult.getBlockPos();
			RiftBlockEntity rift = (RiftBlockEntity) world.getBlockEntity(blockHitResult.getBlockPos());

			world.setBlockAndUpdate(pos, ModBlocks.DIMENSIONAL_PORTAL.defaultBlockState().setValue(DimensionalPortalBlock.FACING, blockHitResult.getDirection()));
			((EntranceRiftBlockEntity) world.getBlockEntity(pos)).setData(rift.getData());

			stack.hurtAndBreak(1, player, a -> a.broadcastBreakEvent(hand));
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
		}
		return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
	}
}
