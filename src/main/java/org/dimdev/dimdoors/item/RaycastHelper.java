package org.dimdev.dimdoors.item;

import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.BlockView;

public final class RaycastHelper {
	public static final int REACH_DISTANCE = 5;

	public static boolean hitsDetachedRift(HitResult hit, BlockView world) {
		return hit != null && hit.getType() == HitResult.Type.BLOCK && world.getBlockEntity(((BlockHitResult) hit).getBlockPos()) instanceof DetachedRiftBlockEntity;
	}

	public static boolean hitsRift(HitResult hit, BlockView world) {
		return hit != null && hit.getType() == HitResult.Type.BLOCK && world.getBlockEntity(((BlockHitResult) hit).getBlockPos()) instanceof RiftBlockEntity;
	}

	public static boolean hitsLivingEntity(HitResult hit) {
		return hit != null && hit.getType() == HitResult.Type.ENTITY && ((EntityHitResult) hit).getEntity() instanceof LivingEntity;
	}
}
