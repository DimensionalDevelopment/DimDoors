package org.dimdev.dimdoors.item

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity
import org.dimdev.dimdoors.block.entity.Rift
import org.joml.Vector3d
import java.util.function.BiConsumer
import java.util.function.Predicate

object RaycastHelper {
    const val REACH_DISTANCE: Int = 16
    @JvmField
    var DETACH: Predicate<BlockEntity> = { blockEntity -> blockEntity is DetachedRiftBlockEntity }
    @JvmField
    var RIFT: Predicate<BlockEntity> = { blockEntity: BlockEntity -> blockEntity is Rift }

    var transformFunction: BiConsumer<Level?, Vector3d?> = BiConsumer { level: Level, pos: Vector3d? -> }

    @JvmStatic
    fun hitsDetachedRift(hit: HitResult?, world: BlockGetter): Boolean {
        return hit != null && hit.getType() == HitResult.Type.BLOCK && world.getBlockEntity((hit as BlockHitResult).getBlockPos()) is DetachedRiftBlockEntity
    }

    @JvmStatic
    fun hitsRift(hit: HitResult?, world: BlockGetter): Boolean {
        return hit != null && hit.type == HitResult.Type.BLOCK && world.getBlockEntity((hit as BlockHitResult).blockPos) is Rift
    }

    @JvmStatic
    fun hitsLivingEntity(hit: HitResult?): Boolean {
        return hit != null && hit.getType() == HitResult.Type.ENTITY && (hit as EntityHitResult).getEntity() is LivingEntity
    }

    fun raycast(entity: Player, tickDelta: Float, predicate: Predicate<Entity?>): HitResult? {
        return raycast(entity, REACH_DISTANCE.toDouble(), tickDelta, predicate)
    }

    fun raycast(entity: Player, maxDistance: Double, tickDelta: Float, predicate: Predicate<Entity?>): HitResult? {
        val vec3d = entity.getEyePosition(tickDelta)
        val vec3d2 = entity.getViewVector(tickDelta)
        val vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance)
        val box = entity.getBoundingBox().expandTowards(vec3d2.scale(maxDistance)).inflate(1.0, 1.0, 1.0)
        return ProjectileUtil.getEntityHitResult(entity, vec3d, vec3d3, box, predicate, maxDistance)
    }

    fun findDetachRift(entity: Entity, predicate: Predicate<BlockEntity>?): BlockHitResult {
        val eye = entity.getEyePosition(0f)
        val viewVec = entity.getViewVector(0f)
        val dest = eye.add(viewVec.x * REACH_DISTANCE, viewVec.y * REACH_DISTANCE, viewVec.z * REACH_DISTANCE)

        RaycastHelper.predicate = predicate
        val result =
            entity.level().clip(ClipContext(eye, dest, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity))
        RaycastHelper.predicate = null
        return result
    }

    @JvmField
    var predicate: Predicate<BlockEntity>? = null

    fun projectileCast(entity: Entity, predicate: Predicate<Entity?>): HitResult {
        RaycastHelper.predicate = DETACH

        var hit : HitResult

        try {
            hit = ProjectileUtil.getHitResultOnMoveVector(entity, predicate)
        } finally {
            RaycastHelper.predicate = null
        }

        return hit
    }
}
