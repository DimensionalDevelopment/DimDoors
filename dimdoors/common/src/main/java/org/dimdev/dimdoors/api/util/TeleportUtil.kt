package org.dimdev.dimdoors.api.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Rotations
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.TicketType
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.border.WorldBorder
import net.minecraft.world.level.portal.DimensionTransition
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.castOrNull
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.entity.stat.ModStats
import org.dimdev.dimdoors.util.LevelSpaceHelper
import org.dimdev.dimdoors.world.ModDimensions
import kotlin.math.abs

object TeleportUtil {
    fun teleport(entity: Entity, world: Level, pos: BlockPos, yaw: Float): Entity {
        return teleport(entity, world, Vec3.atBottomCenterOf(pos), yaw)
    }

    fun teleport(entity: Entity, world: Level, pos: Vec3, yaw: Float): Entity {
        return teleport(entity, world, pos, Rotations(entity.xRot, yaw, 0.0f), entity.deltaMovement)
    }

    fun WorldBorder.clamp(original: Vec3): Vec3 {

        var newX = original.x
        var newZ = original.z
        val size = size - 1
        val northBound = minZ + 1
        val southBound = maxZ - 1
        val westBound = minX + 1
        val eastBound = maxX - 1

        if (newZ < northBound) { newZ = northBound + abs(newZ % size) + 1 } else if (newZ > southBound) { newZ = southBound - abs(newZ % size) - 1 }
        if (newX < westBound) newX = westBound + abs(newX % size) + 1 else if (newX > eastBound) newX = eastBound - abs(newX % size) - 1

        return Vec3(newX, original.y, newZ)
    }

    fun teleport(entity: Entity, world: Level, pos: Vec3, angle: Rotations, velocity: Vec3): Entity {
        var entity: Entity = entity
        var pos = pos

        val serverWorld = world.castOrNull<ServerLevel>() ?: throw UnsupportedOperationException("Only supported on ServerWorld")

        pos = serverWorld.worldBorder.clamp(pos)

        LevelSpaceHelper.INSTANCE.validateTeleportDestination(serverWorld, pos)

        val yaw = Mth.wrapDegrees(angle.getY())
        val pitch = Mth.clamp(Mth.wrapDegrees(angle.getX()), -90.0f, 90.0f)

        val targetPos = BlockPos.containing(pos)
        val chunkPos = ChunkPos(targetPos)

        serverWorld.chunkSource.addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, entity.id)

        when (entity) {
            is ServerPlayer -> {
                entity.stopRiding()

                when {
                    entity.level().dimension() == serverWorld.dimension() -> { entity.teleportTo(serverWorld, pos.x(), pos.y(), pos.z(), mutableSetOf(), yaw, pitch)
                    }

                    else -> {
                        entity = teleport(entity, serverWorld, pos, velocity, yaw, pitch)
                    }
                }

                if (serverWorld.dimension() === ModDimensions.DUNGEON) entity.castOrNull<ServerPlayer>()?.awardStat(ModStats.TIMES_BEEN_TO_DUNGEON)
            }

            else -> {
                if (entity.level().dimension() == serverWorld.dimension()) {
                    entity.moveTo(pos.x(), pos.y(), pos.z(), yaw, pitch)
                } else {
                    entity = teleport(entity, serverWorld, pos, velocity, yaw, pitch)
                }
            }
        }

        entity.deltaMovement = velocity
        return entity
    }

    @JvmStatic
    fun teleport(entity: Entity, world: Level, pos: BlockPos, angle: Rotations, velocity: Vec3): Entity {
        if (world.isClientSide()) {
            throw UnsupportedOperationException("Only supported on ServerWorld")
        }

        return teleport(entity, world, Vec3.atBottomCenterOf(pos), angle, velocity)
    }

    fun teleport(player: ServerPlayer, location: Location): Entity {
        return teleport(player, DimensionalDoors.getWorld(location.worldId)!!, location.blockPos, 0.0f)
    }

    fun teleport(player: ServerPlayer, location: RotatedLocation): Entity {
        return teleport(
            player,
            DimensionalDoors.getWorld(location.worldId)!!,
            location.blockPos,
            location.yaw
        )
    }

    @JvmStatic
    fun teleportRandom(entity: Entity, world: Level, y: Double): Entity {
        val random = RandomSource.create()
        val scale = random.nextGaussian() * random.nextInt(90)
        return teleport(
            entity,
            world,
            entity.position()
                .subtract(0.0, entity.y, 0.0)
                .add(0.0, y, 0.0)
                .multiply(scale, 1.0, scale),
            entity.yRot
        )
    }

    fun teleportUntargeted(entity: Entity, world: Level): Entity {
        val actualScale = entity.level().dimensionType().coordinateScale() / world.dimensionType().coordinateScale()
        return teleport(
            entity,
            world,
            entity.position().multiply(actualScale, 1.0, actualScale),
            entity.yRot
        )
    }

    fun teleportUntargeted(entity: Entity, world: Level, y: Double): Entity {
        val actualScale = entity.level().dimensionType().coordinateScale() / world.dimensionType().coordinateScale()
        return teleport(
            entity,
            world,
            entity.position()
                .subtract(0.0, entity.position().y(), 0.0)
                .add(0.0, y, 0.0)
                .multiply(actualScale, 1.0, actualScale),
            entity.yRot
        )
    }

    fun <E : Entity> teleport(
        entity: E,
        world: ServerLevel,
        pos: Vec3,
        velocity: Vec3,
        yaw: Float,
        pitch: Float
    ): Entity = entity.changeDimension(DimensionTransition(world, pos, velocity, yaw, pitch) {})!!
}
