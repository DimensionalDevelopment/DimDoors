package org.dimdev.dimdoors.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.DimensionalDoors.Companion.config
import org.dimdev.dimdoors.entity.ai.MonolithAggroGoal
import org.dimdev.dimdoors.sound.ModSoundEvents
import org.dimdev.dimdoors.tag.ModWorldTags
import org.dimdev.dimdoors.util.TagUtils
import org.dimdev.dimdoors.world.ModDimensions.isLimboDimension

class MonolithEntity(type: EntityType<out MonolithEntity>, world: Level) : Mob(type, world) {
    private var soundTime = 0
    private val aggroCap: Int

    init {
        this.noPhysics = true
        this.aggroCap = Mth.nextInt(this.getRandom(), MIN_AGGRO_CAP, MAX_AGGRO_CAP)
        this.isNoGravity = true
        this.lookControl = object : LookControl(this) {
            override fun rotateTowards(from: Float, to: Float, maxDelta: Float) = to

            override fun resetXRotOnTick() = false
        }

        this.isInvulnerable = true
    }

    val isDangerous: Boolean
        get() = config.monolithsConfig.monolithTeleportation && (isLimboDimension(
            this.level()
        ) || config.monolithsConfig.dangerousLimboMonoliths)

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if (!source.`is`(DamageTypes.IN_WALL)) this.aggro = MAX_AGGRO
        return false
    }

    override fun decreaseAirSupply(i: Int) = 10

    override fun increaseAirSupply(i: Int) = 10

    //    @Override
    //    public Box getCollisionBox() {
    //        return null;
    //    }
    //
    //    @Override
    //    public Box getHardCollisionBox(Entity entity) {
    //        return null;
    //    }
    override fun requiresCustomPersistence() = false

    override fun isPushable() = false

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        // Add a short for the aggro level
        builder.define(AGGRO, 0)
        builder.define(PITCH, 1f)
        builder.define(SOLID, true)
        //    this.refreshDimensions();
    }

    override fun isAlive(): Boolean {
        return false
    }

    override fun move(moverType: MoverType, vec3: Vec3) {
    }

    override fun customServerAiStep() {
        // Remove this Monolith if it's not in Limbo or in a pocket dungeon
        if (!(TagUtils.isIn(this.level(), ModWorldTags.MONOLITHS_CAN_EXIST))) {
            this.remove(RemovalReason.DISCARDED)
            super.customServerAiStep()
            return
        }

        super.customServerAiStep()

        // Check for players and update aggro levels even if there are no players in range
    }

    fun updateAggroLevel(player: Player?, visibility: Boolean) {
        // If we're working on the server side, adjust aggro level
        // If we're working on the client side, retrieve aggro level from dataWatcher
        if (player == null) {
            return
        }

        //        if ((player.getInventory().armor.get(0).getItem() == ModItems.WORLD_THREAD_HELMET && player.getInventory().armor.get(1).getItem() == ModItems.WORLD_THREAD_CHESTPLATE && player.getInventory().armor.get(2).getItem() == ModItems.WORLD_THREAD_LEGGINGS && player.getInventory().armor.get(3).getItem() == ModItems.WORLD_THREAD_BOOTS)) {
//            return;
//        }
        if (!this.level().isClientSide) {
            if (player.distanceTo(this) > 70) {
                return
            }

            var aggro = this.entityData.get(AGGRO)
            // Server side...
            // Rapidly increase the aggro level if this Monolith can see the player
            if (visibility) {
                if (isLimboDimension(this.level())) {
                    if (this.isDangerous) {
                        aggro++
                    } else {
                        aggro += 36
                    }
                } else {
                    // Aggro increases faster outside of Limbo
                    aggro += 3
                }
            } else {
                if (this.isDangerous) {
                    if (aggro > this.aggroCap) {
                        // Decrease aggro over time
                        aggro--
                    } else if (aggro < this.aggroCap) {
                        // Increase aggro if a player is within range and aggro < aggroCap
                        aggro++
                    }
                } else {
                    aggro -= 3
                }
            }
            // Clamp the aggro level
            val maxAggro = if (this.isDangerous) MAX_AGGRO else 180
            aggro = Mth.clamp(aggro, 0, maxAggro).toShort().toInt()
            this.entityData.set(AGGRO, aggro)
        }
    }

    val textureState: Int get() = Mth.clamp(MAX_TEXTURE_STATE * this.entityData.get(AGGRO) / MAX_AGGRO, 0, MAX_TEXTURE_STATE)

    /**
     * Plays sounds at different levels of aggro, using soundTime to prevent too many sounds at once.
     * 
     * @param pos The position to play the sounds at
     */
    fun playSounds(pos: Vec3) {
        val aggroPercent = this.aggroProgress
        val pitch = this.pitch
        if (this.soundTime <= 0) {
            this.playSound(ModSoundEvents.MONK, 1f, pitch)
            this.soundTime = 100
        }
        if (aggroPercent > 0.70 && this.soundTime < 100) {
            this.level().playSound(
                null,
                BlockPos(Vec3i(pos.x.toInt(), pos.y.toInt(), pos.z.toInt())),
                ModSoundEvents.TEARING,
                SoundSource.HOSTILE,
                1f,
                (1 + this.getRandom().nextGaussian()).toFloat()
            )
            this.soundTime = 100 + this.getRandom().nextInt(75)
        }
        if (aggroPercent > 0.80 && this.soundTime < MAX_SOUND_COOLDOWN) {
            this.level().playSound(
                null,
                BlockPos(Vec3i(pos.x.toInt(), pos.y.toInt(), pos.z.toInt())),
                ModSoundEvents.TEARING,
                SoundSource.HOSTILE,
                7f,
                1f
            )
            this.soundTime = 250
        }
        this.soundTime--
    }

    val aggroProgress: Float
        //    TODO: Figure out if these are needed.
        get() = (this.aggro.toFloat()) / MAX_AGGRO

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(0, MonolithAggroGoal(this, MAX_AGGRO_RANGE.toFloat()))
    }

    fun facePlayer(player: Player) {
        this.lookControl.setLookAt(player, 1.0f, 1.0f)
    }

    override fun addAdditionalSaveData(nbt: CompoundTag) {
        super.addAdditionalSaveData(nbt)
        nbt.putInt("Aggro", this.aggro)
        //        nbt.putFloat("scale", getScale());
        nbt.putFloat("pitch", this.pitch)
    }

    override fun readAdditionalSaveData(nbt: CompoundTag) {
        super.readAdditionalSaveData(nbt)
        this.aggro = nbt.getInt("Aggro")
        if (nbt.contains("pitch", Tag.TAG_FLOAT.toInt())) {
            this.pitch = nbt.getFloat("pitch")
        }

        if (nbt.contains("solid", Tag.TAG_BYTE.toInt())) {
            this.solid = nbt.getBoolean("solid")
        }
    }

    var solid: Boolean
        get() = this.entityData.get(SOLID)
        private set(solid) = this.entityData.set(SOLID, solid)

    var aggro: Int
        get() = this.entityData.get(AGGRO)
        set(aggro) = this.entityData.set(AGGRO, aggro)

    //    @Override
    //    public float getScale() {
    //        return this.entityData.get(SCALE);
    //    }

//    fun setScale(scale: Float) {
//        this.entityData.set(SCALE, scale);
//        getAttribute(Attributes.SCALE)?.baseValue = scale.toDouble()
//        refreshDimensions()
//    }

    var pitch: Float
        get() = this.entityData.get(PITCH)
        set(pitch) = this.entityData.set(PITCH, pitch)

    override fun getLocalBoundsForPose(pose: Pose): AABB {
        val scale = getScale()
        return super.getLocalBoundsForPose(pose).inflate(scale.toDouble(), scale.toDouble(), scale.toDouble())
    }

    override fun onSyncedDataUpdated(data: EntityDataAccessor<*>) {
//    if (SCALE.equals(data)) {
//        this.refreshDimensions();
//    }

        super.onSyncedDataUpdated(data)
    }

    override fun checkSpawnRules(world: LevelAccessor, spawnReason: MobSpawnType): Boolean {
        //TODO: Verify that isn't an situations where world isn't an instance of Level where relevant to monolith spawning.
        if (world is Level && TagUtils.isIn(world, ModWorldTags.MONOLITHS_CAN_EXIST)) {
            if (spawnReason == MobSpawnType.CHUNK_GENERATION) {
                return super.checkSpawnRules(world, spawnReason)
            }
            if (spawnReason == MobSpawnType.NATURAL) {
                return this.getRandom().nextInt(32) == 2
            }
        }

        return false
    }

    companion object {
        const val MAX_AGGRO: Int = 250
        private const val MAX_AGGRO_CAP = 100
        private const val MIN_AGGRO_CAP = 25
        private const val MAX_TEXTURE_STATE = 18
        private const val MAX_SOUND_COOLDOWN = 200
        const val MAX_AGGRO_RANGE: Int = 35
        private val AGGRO = SynchedEntityData.defineId(MonolithEntity::class.java, EntityDataSerializers.INT)

        //    private static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(MonolithEntity.class, EntityDataSerializers.FLOAT);
        private val PITCH = SynchedEntityData.defineId(MonolithEntity::class.java, EntityDataSerializers.FLOAT)
        private val SOLID = SynchedEntityData.defineId(MonolithEntity::class.java, EntityDataSerializers.BOOLEAN)
    }
}
