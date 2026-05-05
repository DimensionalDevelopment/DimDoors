package org.dimdev.dimdoors.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.joml.Vector3d;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class RaycastHelper {
    public static final int REACH_DISTANCE = 16;
    public static Predicate<BlockEntity> DETACH = blockEntity -> blockEntity instanceof DetachedRiftBlockEntity;
    public static Predicate<BlockEntity> RIFT = blockEntity -> blockEntity instanceof RiftBlockEntity;

    public static BiConsumer<Level, Vector3d> transformFunction = (level, pos) -> {};

    public static boolean hitsDetachedRift(HitResult hit, BlockGetter world) {
        return hit != null && hit.getType() == HitResult.Type.BLOCK && world.getBlockEntity(((BlockHitResult) hit).getBlockPos()) instanceof DetachedRiftBlockEntity;
    }

    public static boolean hitsRift(HitResult hit, BlockGetter world) {
        return hit != null && hit.getType() == HitResult.Type.BLOCK && world.getBlockEntity(((BlockHitResult) hit).getBlockPos()) instanceof RiftBlockEntity;
    }

    public static boolean hitsLivingEntity(HitResult hit) {
        return hit != null && hit.getType() == HitResult.Type.ENTITY && ((EntityHitResult) hit).getEntity() instanceof LivingEntity;
    }

    public static HitResult raycast(Player entity, float tickDelta, Predicate<Entity> predicate) {
        return raycast(entity, REACH_DISTANCE, tickDelta, predicate);
    }

    public static HitResult raycast(Player entity, double maxDistance, float tickDelta, Predicate<Entity> predicate) {
        Vec3 vec3d = entity.getEyePosition(tickDelta);
        Vec3 vec3d2 = entity.getViewVector(tickDelta);
        Vec3 vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
        var box = entity.getBoundingBox().expandTowards(vec3d2.scale(maxDistance)).inflate(1.0D, 1.0D, 1.0D);
        return ProjectileUtil.getEntityHitResult(entity, vec3d, vec3d3, box, predicate, maxDistance);
    }

    public static BlockHitResult findDetachRift(Entity entity, Predicate<BlockEntity> clazz) {
        Vec3 eye = entity.getEyePosition(0);
        Vec3 viewVec = entity.getViewVector(0);
        Vec3 dest = eye.add(viewVec.x * RaycastHelper.REACH_DISTANCE, viewVec.y * RaycastHelper.REACH_DISTANCE, viewVec.z * RaycastHelper.REACH_DISTANCE);
        return entity.level().clip(new ClipContext(eye, dest, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity));
    }

}
