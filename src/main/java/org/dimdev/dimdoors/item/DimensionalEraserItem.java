package org.dimdev.dimdoors.item;

import static org.dimdev.dimdoors.api.util.math.MathUtil.entityEulerAngle;

import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.rift.targets.LimboTarget;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DimensionalEraserItem extends Item {
	public DimensionalEraserItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);

		HitResult hit = RaycastHelper.raycast(player, RaycastHelper.REACH_DISTANCE, 1.0F, a -> !(a instanceof PlayerEntity));

		if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
			if(((EntityHitResult) hit).getEntity() instanceof ServerPlayerEntity) {
				BlockPos teleportPos = ((EntityHitResult) hit).getEntity().getBlockPos();
				while(ModDimensions.LIMBO_DIMENSION.getBlockState(VirtualLocation.getTopPos(ModDimensions.LIMBO_DIMENSION, teleportPos.getX(), teleportPos.getZ())).getBlock() == ModBlocks.ETERNAL_FLUID) {
					teleportPos = teleportPos.add(1, 0, 1);
				}
				TeleportUtil.teleport(((EntityHitResult) hit).getEntity(), ModDimensions.LIMBO_DIMENSION, teleportPos.withY(255), entityEulerAngle(((EntityHitResult) hit).getEntity()), ((EntityHitResult) hit).getEntity().getVelocity());
			}

			((EntityHitResult) hit).getEntity().remove(Entity.RemovalReason.KILLED);
			player.playSound(ModSoundEvents.BLOOP, 1.0f, 1.0f);
			return new TypedActionResult<>(ActionResult.SUCCESS, stack);
		}

		return new TypedActionResult<>(ActionResult.FAIL, stack);
	}
}
