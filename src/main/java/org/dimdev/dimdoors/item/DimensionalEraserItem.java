package org.dimdev.dimdoors.item;

import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import static org.dimdev.dimdoors.api.util.math.MathUtil.entityEulerAngle;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class DimensionalEraserItem extends Item {
	public DimensionalEraserItem(Properties settings) {
		super(settings);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		HitResult hit = RaycastHelper.raycast(player, RaycastHelper.REACH_DISTANCE, 1.0F, a -> !(a instanceof Player));

		if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
			if(((EntityHitResult) hit).getEntity() instanceof ServerPlayer) {
				BlockPos teleportPos = ((EntityHitResult) hit).getEntity().blockPosition();
				while(ModDimensions.LIMBO_DIMENSION.getBlockState(VirtualLocation.getTopPos(ModDimensions.LIMBO_DIMENSION, teleportPos.getX(), teleportPos.getZ())).getBlock() == ModBlocks.ETERNAL_FLUID) {
					teleportPos = teleportPos.offset(1, 0, 1);
				}
				TeleportUtil.teleport(((EntityHitResult) hit).getEntity(), ModDimensions.LIMBO_DIMENSION, teleportPos.atY(255), entityEulerAngle(((EntityHitResult) hit).getEntity()), ((EntityHitResult) hit).getEntity().getDeltaMovement());
			}

			((EntityHitResult) hit).getEntity().remove(Entity.RemovalReason.KILLED);
			player.playSound(ModSoundEvents.BLOOP, 1.0f, 1.0f);
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
		}

		return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
	}
}
