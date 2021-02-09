package org.dimdev.dimdoors.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.sound.ModSoundEvents;

public class DimensionalEraser extends Item {
	public DimensionalEraser(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);

		HitResult hit = RaycastHelper.raycast(player, RaycastHelper.REACH_DISTANCE, 1.0F, a -> !(a instanceof PlayerEntity));

		if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
			((EntityHitResult) hit).getEntity().remove();
			player.playSound(ModSoundEvents.BLOOP, 1.0f, 1.0f);
			return new TypedActionResult<>(ActionResult.SUCCESS, stack);
		}

		return new TypedActionResult<>(ActionResult.FAIL, stack);
	}
}
