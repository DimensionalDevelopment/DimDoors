package org.dimdev.dimdoors.block.entity

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.Rotations
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.entity.monster.EnderMan
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.DimensionalDoors.Companion.config
import org.dimdev.dimdoors.api.rift.target.EntityTarget
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.TeleportUtil
import org.dimdev.dimdoors.client.RiftCurves
import org.dimdev.dimdoors.rift.registry.Vertex
import org.dimdev.dimdoors.util.LevelSpaceHelper
import org.dimdev.dimdoors.util.Utils
import org.dimdev.dimdoors.world.decay.Decay
import org.dimdev.dimdoors.world.decay.DecaySource
import java.util.function.Consumer
import kotlin.math.abs

class DetachedRiftBlockEntity(pos: BlockPos, state: BlockState) : RiftBlockEntity<DetachedRiftBlockEntity>(
    ModBlockEntityTypes.DETACHED_RIFT, pos, state), EntityTarget {
    var spawnedEndermanId: Int = 0
    @JvmField
    var riftYaw: Float
    @JvmField
    var curveID: Int
    private var weight = 5
    private var updateTimer = 0

    init {
        this.curveID = (Math.random() * RiftCurves.CURVES.size).toInt()
        this.riftYaw = (Math.random() * 360).toFloat()
    }

    /**
     * Checks the blocks around the location of the floating rift and applies the decay
     */
    fun applySpreadDecay(world: ServerLevel, pos: BlockPos) {
        val radius = this.decayRadius
        if (radius <= 0) return

        val selected = Utils.randomInSphere(world.getRandom(), 1, pos, radius).iterator().next()
        if (selected == pos) return

        Decay.decayBlock(
            world,
            pos,
            world.getBlockState(pos),
            selected,
            world.getBlockState(selected),
            DecaySource.RIFT
        )
    }

    fun setClosing() {
        this.setWeight(-100)
    }

    fun setStabilized() {
        this.setWeight(0)
    }

    fun getWeight(): Int {
        return weight
    }

    val decayRadius: Int
        get() {
            val size = data.size
            return if (size > 0) Mth.ceil(size / DECAY_RADIUS_DIVISOR) else 0
        }

    fun setWeight(weight: Int) {
        this.weight = Mth.clamp(weight, -100, 100)
        this.setChanged()
    }

    override fun gatherDebug(textConsumer: Consumer<Component>) {
        super.gatherDebug(textConsumer)
        textConsumer.accept(Component.literal("Decay radius: " + this.decayRadius))
    }

    override fun deserialize(nbt: Deserialize<Tag>) {
        super.deserialize(nbt)
        spawnedEndermanId = nbt.get<DetachedRiftBlockEntity, Int>(SPAWNED_ENDERMAN_ID_BUILDER)
        curveID = nbt.get<DetachedRiftBlockEntity, Int>(CURVE_ID_BUILDER)
        riftYaw = nbt.get<DetachedRiftBlockEntity, Float>(RIFT_YAW_BUILDER)
        weight = nbt.get<DetachedRiftBlockEntity, Int>(WEIGHT_BUILDER)
        updateTimer = nbt.get<DetachedRiftBlockEntity, Int>(UPDATE_TIMER_BUILDER)
    }


    override fun serialize(serialize: Serialize<Tag, DetachedRiftBlockEntity>) {
        super.serialize(serialize)
        serialize.put(SPAWNED_ENDERMAN_ID_BUILDER)
        serialize.put(CURVE_ID_BUILDER)
        serialize.put(RIFT_YAW_BUILDER)
        serialize.put(WEIGHT_BUILDER)
        serialize.put(UPDATE_TIMER_BUILDER)
    }

    override fun prepareTag(nbt: CompoundTag) {
        super.prepareTag(nbt)
        if (nbt.contains("closing", Tag.TAG_ANY_NUMERIC.toInt()) || nbt.contains(
                "stablized",
                Tag.TAG_ANY_NUMERIC.toInt()
            )
        ) {
            var weight = nbt.getInt("weight")

            if (nbt.getBoolean("stablized")) weight = 0
            if (nbt.getBoolean("closing")) weight = -100

            nbt.putInt("weight", weight)
        }
    }

    override val isDetached: Boolean get() = true

    override fun unregister() {
        super.unregister()
        level?.removeBlock(blockPos, false)
    }

    override fun receiveEntity(
        owner: Vertex,
        entity: Entity,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3,
        location: Location?
    ): Boolean {
        if (this.level is ServerLevel) {
            val localTargetPos = Vec3.atBottomCenterOf(this.worldPosition)

            val frame =
                LevelSpaceHelper.INSTANCE.projectTeleportFrame(level as ServerLevel, location, localTargetPos, relativeAngle, relativeVelocity)

            TeleportUtil.teleport(entity, this.level as ServerLevel, frame.pos, frame.angle, frame.velocity)
        }
        return true
    }

    override fun update(level: Level, pos: BlockPos, blockState: BlockState) {
        if (weight != 0) {
            val absoluteChance = abs(weight)

            if (updateTimer % 20 == 0) {
                if (level.random.nextInt(0, 100) <= absoluteChance) {
                    val sizeChange = if (weight > 0) 1 else -1

                    data.size += sizeChange
                }

                if (weight < 0 && data.size == 0) {
                    unregister()
                    return
                }

                updateTimer = 0
                sync()

                if (config.generalConfig.enableRiftDecay && data.size > 20) {
                    applySpreadDecay(level as ServerLevel, pos)
                }

                tryEndermanSpawn(level, pos)
            }


            updateTimer++
        }
    }

    override fun getUpdateTag(provider: HolderLookup.Provider): CompoundTag {
        val tag = super.getUpdateTag(provider)
        tag.putInt("weight", this.weight)
        tag.putInt("curveID", this.curveID)
        tag.putFloat("rotation", this.riftYaw)
        return tag
    }

    private fun tryEndermanSpawn(level: Level, pos: BlockPos) {
        if (level.getEntity(spawnedEndermanId) is EnderMan) {
            return
        }

        if (level.random.nextFloat() < config.generalConfig.endermanSpawnChance) {
            val list = level.getEntitiesOfClass(
                EnderMan::class.java,
                AABB(
                    (pos.x - 9).toDouble(),
                    (pos.y - 3).toDouble(),
                    (pos.z - 9).toDouble(),
                    (pos.x + 9).toDouble(),
                    (pos.y + 3).toDouble(),
                    (pos.z + 9).toDouble()
                )
            )

            if (list.isEmpty()) {
                val enderman = EntityType.ENDERMAN.spawn(level as ServerLevel, pos, MobSpawnType.STRUCTURE) ?: return
                enderman.absMoveTo(pos.x + 0.5, (pos.y - 1).toDouble(), pos.z + 0.5, 5f, 6f)

                if (level.random.nextDouble() < config.generalConfig.endermanAggressiveChance) {
                    level.getNearestPlayer(enderman, 50.0)?.run { enderman.target = this }
                }
            }
        }
    }

    companion object {
        const val DECAY_RADIUS_DIVISOR: Float = 40f
        private val SPAWNED_ENDERMAN_ID_BUILDER = CodecRecord<DetachedRiftBlockEntity, Int>("spawnedEnderManId", Codec.INT, 0, DetachedRiftBlockEntity::spawnedEndermanId)
        private val RIFT_YAW_BUILDER = CodecRecord<DetachedRiftBlockEntity, Float>("rotation", Codec.FLOAT, { (Math.random() * 360).toFloat() }, DetachedRiftBlockEntity::riftYaw)
        private val CURVE_ID_BUILDER = CodecRecord<DetachedRiftBlockEntity, Int>("curveID", Codec.INT, { (Math.random() * RiftCurves.CURVES.size).toInt() }, DetachedRiftBlockEntity::curveID)
        private val WEIGHT_BUILDER = CodecRecord<DetachedRiftBlockEntity, Int>("weight", Codec.intRange(-100, 100), 5, DetachedRiftBlockEntity::weight)
        private val UPDATE_TIMER_BUILDER = CodecRecord<DetachedRiftBlockEntity, Int>("updateTimer", Codec.INT, 0, DetachedRiftBlockEntity::updateTimer)
    }
}
