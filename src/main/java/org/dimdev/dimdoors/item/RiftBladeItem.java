package org.dimdev.dimdoors.item;

import java.util.List;
import java.util.Objects;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.DimensionalPortalBlock;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.client.ToolTipHelper;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RiftBladeItem extends SwordItem {
	public static final String ID = "rift_blade";

	public RiftBladeItem(Settings settings) {
		super(ToolMaterials.IRON, 3, -2.4F, settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack itemStack, World world, List<Text> list, TooltipContext tooltipContext) {
		ToolTipHelper.processTranslation(list, this.getTranslationKey() + ".info");
	}

	@Override
	public boolean hasGlint(ItemStack itemStack) {
		return true;
	}

	@Override
	public boolean canRepair(ItemStack item, ItemStack repairingItem) {
		return Objects.equals(ModItems.STABLE_FABRIC, repairingItem.getItem());
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		HitResult hit = RaycastHelper.raycast(player, 16, 0.0F, LivingEntity.class::isInstance);

		if (hit == null) {
			hit = RaycastHelper.raycast(player, 16, 1.0F, LivingEntity.class::isInstance);
		}

		if (hit == null) {
			hit = player.raycast(16, 1.0F, false); //TODO: make the range of the Rift Blade configurable
		}

		if (hit == null) {
			hit = player.raycast(16, 0, false);
		}

		if (world.isClient) {
			if (RaycastHelper.hitsLivingEntity(hit) || RaycastHelper.hitsRift(hit, world)) {
				return new TypedActionResult<>(ActionResult.SUCCESS, stack);
			} else {
				player.sendMessage(new TranslatableText(this.getTranslationKey() + ".rift_miss"), true);
				RiftBlockEntity.showRiftCoreUntil = System.currentTimeMillis() + DimensionalDoorsInitializer.getConfig().getGraphicsConfig().highlightRiftCoreFor;
				return new TypedActionResult<>(ActionResult.FAIL, stack);
			}
		}

		if (RaycastHelper.hitsLivingEntity(hit)) {
			double damageMultiplier = (double) stack.getDamage() / (double) stack.getMaxDamage();
			// TODO: gaussian, instead or random
			double offsetDistance = Math.random() * damageMultiplier * 7 + 2; //TODO: make these offset distances configurable
			double offsetRotationYaw = (Math.random() - 0.5) * damageMultiplier * 360;

			Vec3d playerVec = player.getPos();
			Vec3d entityVec = hit.getPos();
			Vec3d offsetDirection = playerVec.subtract(entityVec).normalize();
			offsetDirection = offsetDirection.rotateY((float) (offsetRotationYaw * Math.PI) / 180);

			BlockPos teleportPosition = new BlockPos(entityVec.add(offsetDirection.multiply(offsetDistance)));
			while (world.getBlockState(teleportPosition).getMaterial().blocksMovement())
				teleportPosition = teleportPosition.up();
			player.teleport(teleportPosition.getX(), teleportPosition.getY(), teleportPosition.getZ());
			player.setYaw((float) (Math.random() * 2 * Math.PI));

			stack.damage(1, player, a -> a.sendToolBreakStatus(hand));
			return new TypedActionResult<>(ActionResult.SUCCESS, stack);
		} else if (RaycastHelper.hitsDetachedRift(hit, world)) {
			BlockHitResult blockHitResult = (BlockHitResult) hit;
			BlockPos pos = blockHitResult.getBlockPos();
			RiftBlockEntity rift = (RiftBlockEntity) world.getBlockEntity(blockHitResult.getBlockPos());

			world.setBlockState(pos, ModBlocks.DIMENSIONAL_PORTAL.getDefaultState().with(DimensionalPortalBlock.FACING, blockHitResult.getSide()));
			((EntranceRiftBlockEntity) world.getBlockEntity(pos)).setData(rift.getData());

			stack.damage(1, player, a -> a.sendToolBreakStatus(hand));
			return new TypedActionResult<>(ActionResult.SUCCESS, stack);
		}
		return new TypedActionResult<>(ActionResult.FAIL, stack);
	}
}
