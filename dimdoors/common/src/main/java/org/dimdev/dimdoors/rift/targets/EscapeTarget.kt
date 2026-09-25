package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Rotations
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimcore.api.util.EntityUtils
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.DimensionalDoors.Companion.getConfig
import org.dimdev.dimdoors.DimensionalDoors.Companion.server
import org.dimdev.dimdoors.api.rift.target.EntityTarget
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.Location.Companion.getHeightmapPosSafe
import org.dimdev.dimdoors.api.util.Location.Companion.ofWorld
import org.dimdev.dimdoors.api.util.TeleportUtil
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.decay.Decay
import org.dimdev.dimdoors.world.decay.DecaySource
import java.util.*

class EscapeTarget(protected val canEscapeLimbo: Boolean) : VirtualTarget<EscapeTarget>(), EntityTarget {
    override fun receiveEntity(
        entity: Entity,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3,
        location: Location?
    ): Boolean {
        var entity = entity
        if (!ModDimensions.isPocketDimension(entity.level()) && !(ModDimensions.isLimboDimension(entity.level()))) {
//        chat(entity, Component.translatable("rifts.destinations.escape.not_in_pocket_dim")); TODO: Decide a proper alternate to spam
            return false
        }
        if (ModDimensions.isLimboDimension(entity.level()) && !this.canEscapeLimbo) {
//        chat(entity, Component.translatable("rifts.destinations.escape.cannot_escape_limbo")); TODO: Decide a proper alternate to spam
            return false
        }


        if (entity.level().isClientSide) return false
        if (entity is ServerPlayer) { //TODO: Determine what other entity types should do when escaping.

            var destLevel: ServerLevel? = null
            var destPos: BlockPos? = null

            if (getConfig().limboConfig.tryPlayerBedSpawn) {
                val level = DimensionalDoors.getWorld(entity.respawnDimension)

                if (level != null) {
                    destLevel = level
                    destPos = entity.respawnPosition
                }
            }


            if (destLevel == null) {
                val targetWorld = getConfig().limboConfig.escapeTargetWorld
                destLevel = server.overworld()

                val level = DimensionalDoors.getWorld(targetWorld)

                if (level != null) {
                    destLevel = level
                }

                destPos =
                    (if (getConfig().limboConfig.defaultToWorldSpawn) destLevel.sharedSpawnPos else entity.blockPosition())
            }

            /*
        if (destLoc != null && destLoc.getBlockEntity() instanceof RiftBlockEntity || this.canEscapeLimbo) {
        //Location location = VirtualLocation.fromLocation(new Location((ServerWorld) entity.world, entity.getBlockPos())).projectToWorld(false);
        TeleportUtil.teleport(entity, destLoc.getWorld(), destLoc.getBlockPos(), relativeAngle, relativeVelocity);
        } else {
        if (destLoc == null) {
            chat(entity, MutableText.of(new TranslatableTextContent("rifts.destinations.escape.did_not_use_rift"));
        } else {
            chat(entity, MutableText.of(new TranslatableTextContent("rifts.destinations.escape.rift_has_closed"));
        }
        if (ModDimensions.LIMBO_DIMENSION != null) {
            TeleportUtil.teleport(entity, ModDimensions.LIMBO_DIMENSION, new BlockPos(this.location.getX(), this.location.getY(), this.location.getZ()), relativeAngle, relativeVelocity);
        }
        }
         */

            val destLoc: Location? = randomizeLimboReturn(
                destLevel,
                destPos,
                getConfig().limboConfig.limboReturnDistanceMin,
                getConfig().limboConfig.limboReturnDistanceMax
            ) //todo add minimum radius

            if (destLoc != null && this.canEscapeLimbo) {
                val location =
                    destLoc //VirtualLocation.fromLocation(new Location((ServerWorld) entity.world, destLoc.pos)).projectToWorld(false); //TODO Fix world projection.

                val level = location.world
                entity = TeleportUtil.teleport(entity, level, location.blockPos, relativeAngle, relativeVelocity)
                entity.fallDistance = -500f
                level.setBlockAndUpdate(location.blockPos, Blocks.AIR.defaultBlockState())
                level.setBlockAndUpdate(location.blockPos.offset(0, 1, 0), Blocks.AIR.defaultBlockState())

                if (getConfig().limboConfig.decaySurroundings) {
                    val random = RandomSource.create()
                    BlockPos.withinManhattan(location.blockPos.offset(0, -3, 0), 3, 2, 3)
                        .forEach { pos ->
                            if (random.nextFloat() < (1 / (location.blockPos.distSqr(pos)
                                    .toFloat())) * getConfig().limboConfig.limboBlocksCorruptingExitWorldAmount
                            ) {
                                decayBlock(level, pos)
                            }
                        }
                }
            } else {
                EntityUtils.chat(
                    entity,
                    Component.translatable(if (destLoc == null) "rifts.destinations.escape.did_not_use_rift" else "rifts.destinations.escape.rift_has_closed")
                )

                if (ModDimensions.LIMBO_DIMENSION != null) {
                    entity = TeleportUtil.teleport(
                        entity,
                        ModDimensions.LIMBO_DIMENSION,
                        BlockPos(this.location!!.x, this.location!!.y, this.location!!.z),
                        relativeAngle,
                        relativeVelocity
                    )
                    entity.fallDistance = -500f
                }
            }
            return true
        } else {
            return false // No escape info for that entity
        }
    }

    override val type get() = VirtualTargets.ESCAPE

    override fun copy(): EscapeTarget {
        return EscapeTarget(canEscapeLimbo)
    }

    companion object {
        // TODO: createRift option
        private val LOGGER: Logger? = LogManager.getLogger()
        private val targetWorldResourceKey: ResourceKey<Level?>? = null

        val CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group(
                Codec.BOOL.fieldOf("canEscapeLimbo").forGetter(EscapeTarget::canEscapeLimbo)
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
                randomizeCoord(pos.getX(), minRange, maxRange),
                randomizeCoord(pos.getZ(), minRange, maxRange)
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
