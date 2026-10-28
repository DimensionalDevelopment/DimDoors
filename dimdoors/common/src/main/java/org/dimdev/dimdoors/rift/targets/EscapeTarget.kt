package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Rotations
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.errata.schema.Schemas
import org.dimdev.dimcore.api.util.EntityUtils.chat
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.DimensionalDoors.Companion.server
import org.dimdev.dimdoors.api.rift.target.EntityTarget
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.Location.Companion.getHeightmapPosSafe
import org.dimdev.dimdoors.api.util.Location.Companion.ofWorld
import org.dimdev.dimdoors.api.util.TeleportUtil
import org.dimdev.dimdoors.rift.registry.Vertex
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.decay.Decay
import org.dimdev.dimdoors.world.decay.DecaySource
import org.dimdev.dimdoors.world.pocket.VirtualLocation
import java.util.*

class EscapeTarget(val canEscapeLimbo: Boolean) : VirtualTarget<EscapeTarget>(), EntityTarget {
    override fun receiveEntity(
        owner: Vertex,
        entity: Entity,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3,
        location: Location?
    ): Boolean {
        val level = entity.level()

        if (level.isClientSide) return false

        val isLimbo = ModDimensions.isLimboDimension(level)
        val isPocket = ModDimensions.isPocketDimension(level)

        if (!isPocket && !isLimbo) {
            chat(entity, Component.translatable("rifts.destinations.escape.not_in_pocket_dim"))
            return false
        }

        if (entity !is ServerPlayer) return false //TODO: Determine what other entity types should do when escaping.

        if (isLimbo && !canEscapeLimbo) {
            chat(entity, Component.translatable("rifts.destinations.escape.cannot_escape_limbo"))
            return false
        }

        val destLoc = when {
            isLimbo || DimensionalDoors.config.limboConfig.canUseEscapeDoorInLimbo -> limboReturn(entity)
            else -> VirtualLocation.fromLocation(owner).let { root ->
                DimensionalDoors.getWorld(root.world)?.let { ofWorld(it, getHeightmapPosSafe(it, root.x, root.z)) }
            }
        }

        if (destLoc != null) {
            val destLevel = destLoc.world
            TeleportUtil.teleport(
                entity,
                destLevel,
                destLoc.blockPos,
                relativeAngle,
                relativeVelocity
            ).fallDistance = -500f
            destLevel.setBlockAndUpdate(destLoc.blockPos, Blocks.AIR.defaultBlockState())
            destLevel.setBlockAndUpdate(destLoc.blockPos.offset(0, 1, 0), Blocks.AIR.defaultBlockState())

            if (DimensionalDoors.config.limboConfig.decaySurroundings) {
                val random = RandomSource.create()
                BlockPos.withinManhattan(destLoc.blockPos.offset(0, -3, 0), 3, 2, 3)
                    .forEach { pos ->
                        if (random.nextFloat() < (1 / (destLoc.blockPos.distSqr(pos)
                                .toFloat())) * DimensionalDoors.config.limboConfig.limboBlocksCorruptingExitWorldAmount
                        ) {
                            decayBlock(destLevel, pos)
                        }
                    }
            }
        } else {
            chat(
                entity,
                Component.translatable("rifts.destinations.escape.did_not_use_rift")
            )

            val ownerLocation = owner.providedLocation!!

            TeleportUtil.teleport(
                entity,
                ModDimensions.LIMBO_DIMENSION,
                BlockPos(ownerLocation.x, ownerLocation.y, ownerLocation.z),
                relativeAngle,
                relativeVelocity
            ).fallDistance = -500f
        }
        return true
    }

    private fun limboReturn(entity: ServerPlayer): Location? {
        var destLevel: ServerLevel? = null
        var destPos: BlockPos? = null

        if (DimensionalDoors.config.limboConfig.tryPlayerBedSpawn) {
            DimensionalDoors.getWorld(entity.respawnDimension).run {
                destLevel = this
                destPos = entity.respawnPosition
            }
        }


        if (destLevel == null) {
            val targetWorld = DimensionalDoors.config.limboConfig.escapeTargetWorld
            destLevel = server.overworld()

            val level = DimensionalDoors.getWorld(targetWorld)

            if (level != null) {
                destLevel = level
            }

            destPos =
                (if (DimensionalDoors.config.limboConfig.defaultToWorldSpawn) destLevel.sharedSpawnPos else entity.blockPosition())
        }

        return randomizeLimboReturn(
            destLevel,
            destPos,
            DimensionalDoors.config.limboConfig.limboReturnDistanceMin,
            DimensionalDoors.config.limboConfig.limboReturnDistanceMax
        )
    }

    override val type get() = VirtualTargets.ESCAPE

    override fun copy(): EscapeTarget {
        return EscapeTarget(canEscapeLimbo)
    }

    companion object {
        // TODO: createRift option

        val CODEC: MapCodec<EscapeTarget> = RecordCodecBuilder.mapCodec { instance -> instance.group(
            Schemas.BOOL.fieldOf("canEscapeLimbo").forGetter(EscapeTarget::canEscapeLimbo)
        ).apply(
            instance, ::EscapeTarget
        )
        }

        fun of(canEscapeLimbo: Boolean) = EscapeTarget(canEscapeLimbo)
    }

    fun randomizeLimboReturn(level: ServerLevel?, pos: BlockPos?, minRange: Int, maxRange: Int): Location? {
        if (level == null || pos == null) return null

        if (minRange == 0 && maxRange == 0) return ofWorld(level, pos)

        return ofWorld(
            level,
            getHeightmapPosSafe(
                level,
                randomizeCoord(pos.x, minRange, maxRange),
                randomizeCoord(pos.z, minRange, maxRange)
            )
        )
    }

    private fun decayBlock(level: ServerLevel, pos: BlockPos) {
        val state = level.getBlockState(pos)
        val context =
            Decay.DecayContext(level, pos, state, pos, state, state.getFluidState(), null, DecaySource.LIMBO)

        for (pattern in Decay.DecayLoader.getPatterns(context)) {
            if (pattern.value.test(context)) {
                pattern.value.applyPattern(context)
                return
            }
        }
    }

    fun randomizeCoord(coord: Int, minRange: Int, maxRange: Int): Int {
        val random = Random()

        require(minRange <= maxRange) { "minRange cannot be greater than maxRange" }

        val offset = minRange + random.nextInt((maxRange - minRange) + 1)
        val isPositive = random.nextBoolean()

        return if (isPositive) coord + offset else coord - offset
    }
}