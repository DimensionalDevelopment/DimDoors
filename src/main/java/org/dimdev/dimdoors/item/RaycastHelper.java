package org.dimdev.dimdoors.item;

import java.util.function.Predicate;

import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
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

	public static HitResult raycast(Entity entity, double maxDistance, float tickDelta, Predicate<Entity> predicate) {
		Vec3d vec3d = entity.getCameraPosVec(tickDelta);
		Vec3d vec3d2 = entity.getRotationVec(tickDelta);
		Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
		Box box = entity.getBoundingBox().stretch(vec3d2.multiply(maxDistance)).expand(1.0D, 1.0D, 1.0D);
		return ProjectileUtil.raycast(entity, vec3d, vec3d3, box, predicate, maxDistance);
	}
}
