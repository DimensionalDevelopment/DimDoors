package org.dimdev.dimdoors.entity.mask

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.PlacedContent
import java.util.EnumSet

abstract class MaskGoal(protected val mask: MaskEntity, private val state: MaskState) : Goal() {
    init {
        flags = EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK)
    }

    override fun canUse() = !mask.frozen && mask.state == state && applies()

    override fun canContinueToUse() = canUse()

    override fun requiresUpdateEveryTick() = true

    protected open fun applies() = true
}

class MaskChaseGoal(mask: MaskEntity) : MaskGoal(mask, MaskState.CHASING) {
    override fun tick() {
        val target = mask.chaseTarget ?: return
        mask.flyToward(target.position(), if (mask.isInsideBlock) MaskEntity.CHASE_SPEED * MaskEntity.CHASE_SPEED_IN_BLOCK_MULT else MaskEntity.CHASE_SPEED)
    }
}

class MaskReturnGoal(mask: MaskEntity) : MaskGoal(mask, MaskState.RETURNING) {
    override fun tick() {
        val home = mask.home
        if (home == null || mask.flyToward(Vec3.atBottomCenterOf(home), MaskEntity.CHASE_SPEED)) mask.state = MaskState.IDLE
    }
}

class MaskEchoGoal(mask: MaskEntity) : MaskGoal(mask, MaskState.IDLE) {
    private var block: BlockPos? = null

    override fun applies() = mask.maskType == MaskType.ECHO && (block != null || mask.tickCount % 20 == 0 && find())

    override fun tick() {
        val pos = block ?: return
        val level = mask.level() as ServerLevel

        if (!PlacedContent.isPlaced(level, pos) || level.getBlockState(pos).isAir) {
            block = null
        } else if (mask.position().distanceToSqr(Vec3.atCenterOf(pos)) < 4.0) {
            mask.breakPlaced(level, pos)
            block = null
        } else {
            mask.flyToward(Vec3.atBottomCenterOf(pos), MaskEntity.IDLE_SPEED)
        }
    }

    private fun find(): Boolean {
        val level = mask.level()
        val eye = mask.eyePosition
        val range = MaskType.ECHO.detectionRange

        block = BlockPos.betweenClosed(BlockPos.containing(eye.subtract(range, range, range)), BlockPos.containing(eye.add(range, range, range))).firstOrNull { pos ->
            val center = Vec3.atCenterOf(pos)
            PlacedContent.isPlaced(level, pos) && !level.getBlockState(pos).isAir && mask.inSight(center, range) &&
                level.clip(ClipContext(eye, center, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mask)).blockPos == pos
        }?.immutable()

        return block != null
    }
}

class MaskGuardGoal(mask: MaskEntity) : MaskGoal(mask, MaskState.IDLE) {
    private var turn = 0

    override fun applies() = mask.mode == MaskMode.GUARD

    override fun tick() {
        mask.home?.let { mask.flyToward(Vec3.atBottomCenterOf(it), MaskEntity.IDLE_SPEED) }

        if (--turn > 0) return
        turn = 100 + mask.random.nextInt(101)

        val level = mask.level()
        val open = DIRECTIONS.filter { direction ->
            (1..MaskEntity.GUARD_WALL_AVOID_DIST).none { distance ->
                val pos = BlockPos.containing(mask.eyePosition.add(direction.scale(distance.toDouble())))
                !level.getBlockState(pos).getCollisionShape(level, pos).isEmpty
            }
        }

        if (open.isNotEmpty()) mask.face(open[mask.random.nextInt(open.size)])
    }

    companion object {
        private val DIRECTIONS = (0 until 8).map { Vec3.directionFromRotation(0f, it * 45f) }
    }
}

class MaskPatrolGoal(mask: MaskEntity) : MaskGoal(mask, MaskState.IDLE) {
    private var pause = 0

    override fun applies() = mask.mode == MaskMode.PATROL && mask.home != null

    override fun tick() {
        if (pause > 0) {
            pause--
            return
        }

        val home = mask.home ?: return
        val route = listOf(BlockPos.ZERO) + mask.waypoints.reversed().filter { it != BlockPos.ZERO }
        if (route.size < 2) return

        mask.waypointIndex = mask.waypointIndex.coerceIn(0, route.lastIndex)

        if (mask.flyToward(Vec3.atBottomCenterOf(home.offset(route[mask.waypointIndex])), MaskEntity.IDLE_SPEED)) {
            pause = MaskEntity.PATROL_WAYPOINT_PAUSE
            if (mask.waypointIndex == route.lastIndex) mask.waypointReverse = true
            else if (mask.waypointIndex == 0) mask.waypointReverse = false
            mask.waypointIndex += if (mask.waypointReverse) -1 else 1
        } else if (mask.horizontalCollision) {
            mask.breakPlacedAhead()
        }
    }
}

class MaskWanderGoal(mask: MaskEntity) : MaskGoal(mask, MaskState.IDLE) {
    private var destination: Vec3? = null
    private var pause = 0

    override fun applies() = mask.mode == MaskMode.WANDER

    override fun tick() {
        if (pause > 0) {
            pause--
            return
        }

        val destination = destination ?: pick() ?: return

        if (mask.flyToward(destination, MaskEntity.IDLE_SPEED) || mask.horizontalCollision) {
            this.destination = null
            pause = 40 + mask.random.nextInt(60)
        }
    }

    private fun pick(): Vec3? {
        val center = mask.home?.let(Vec3::atBottomCenterOf) ?: mask.position()
        val random = mask.random
        val candidate = center.add((random.nextDouble() * 2 - 1) * MaskEntity.WANDER_RADIUS, (random.nextDouble() * 2 - 1) * 2, (random.nextDouble() * 2 - 1) * MaskEntity.WANDER_RADIUS)

        val clear = mask.level().clip(ClipContext(mask.position(), candidate, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mask)).type == HitResult.Type.MISS
        return candidate.takeIf { clear }?.also { destination = it }
    }
}
