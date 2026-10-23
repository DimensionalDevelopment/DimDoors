package org.dimdev.dimdoors.entity.mask

import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.DamageTypeTags
import net.minecraft.tags.GameEventTags
import net.minecraft.tags.TagKey
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.DynamicGameEventListener
import net.minecraft.world.level.gameevent.EntityPositionSource
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimcore.api.PlacedContent
import org.dimdev.dimcore.api.event.PlayerTeleportEvents
import org.dimdev.dimdoors.ModGameRules
import org.dimdev.dimdoors.effect.ModMobEffects
import org.dimdev.dimdoors.effect.createInstance
import org.dimdev.dimdoors.entity.ModEntityTypes
import org.dimdev.dimdoors.item.MaskWearerEffects
import org.dimdev.dimdoors.item.ModItems
import org.dimdev.dimdoors.network.packet.s2c.MaskCatchAnimS2CPacket
import org.dimdev.dimdoors.rift.registry.PocketRegistry
import org.dimdev.dimdoors.rift.targets.LimboTarget
import org.dimdev.dimdoors.world.DataValues
import org.dimdev.dimdoors.world.ModDimensions
import java.util.*
import java.util.function.BiConsumer
import kotlin.math.cos

class MaskEntity(entityType: EntityType<out MaskEntity>, level: Level? = null) : Mob(entityType, level), VibrationSystem {
    val idleState: AnimationState = AnimationState()
    val spottedState: AnimationState = AnimationState()
    val stunnedState: AnimationState = AnimationState()

    var home: BlockPos? = null
    var waypoints: List<BlockPos> = emptyList()
    var waypointIndex = 0
    var waypointReverse = false

    private var targetId: UUID? = null
    private var stateTimer = 0
    private var chimeTimer = 0
    private var initialized = false

    private val vibrations = VibrationSystem.Data()
    private val gameEventListener = DynamicGameEventListener(VibrationSystem.Listener(this))
    private val listenerUser = object : VibrationSystem.User {
        private val positionSource = EntityPositionSource(this@MaskEntity, eyeHeight)

        override fun getListenerRadius() = MaskType.SCULKING.detectionRange.toInt()

        override fun getPositionSource() = positionSource

        override fun getListenableEvents(): TagKey<GameEvent> = GameEventTags.WARDEN_CAN_LISTEN

        override fun canReceiveVibration(level: ServerLevel, pos: BlockPos, event: Holder<GameEvent>, context: GameEvent.Context): Boolean {
            if (maskType != MaskType.SCULKING || state != MaskState.IDLE || frozen) return false
            val player = vibrationPlayer(context.sourceEntity()) ?: return false
            return canSense(player) && player.distanceTo(this@MaskEntity) <= MaskEvents.DETECTION_RANGE.invoker().invoke(this@MaskEntity, player, maskType.detectionRange)
        }

        override fun onReceiveVibration(level: ServerLevel, pos: BlockPos, event: Holder<GameEvent>, entity: Entity?, projectileOwner: Entity?, distance: Float) {
            vibrationPlayer(projectileOwner ?: entity)?.let(::alert)
        }

        private fun vibrationPlayer(source: Entity?) = ((source as? Projectile)?.owner ?: source) as? ServerPlayer
    }

    init {
        this.isNoGravity = true
        this.lookControl = object : LookControl(this) {
            override fun tick() {}
        }
    }

    var maskType: MaskType
        get() = MaskType.entries[Mth.clamp(entityData.get(TYPE).toInt(), 0, MaskType.entries.size - 1)]
        set(type) = entityData.set(TYPE, type.ordinal.toByte())

    var mode: MaskMode
        get() {
            val id = entityData.get<Byte>(MODE)
            val values = MaskMode.entries.toTypedArray()
            return values[Mth.clamp(id.toInt(), 0, values.size - 1)]
        }
        set(mode) = entityData.set(MODE, mode.ordinal.toByte())

    var state: MaskState
        get() = MaskState.entries[Mth.clamp(entityData.get(STATE).toInt(), 0, MaskState.entries.size - 1)]
        set(state) {
            entityData.set(STATE, state.ordinal.toByte())
            stateTimer = 0
            applyStateFlags(state)
        }

    var frozen: Boolean
        get() = entityData.get(FROZEN)
        set(frozen) = entityData.set(FROZEN, frozen)

    val chaseTarget: ServerPlayer? get() = targetId?.let { level().getPlayerByUUID(it) } as? ServerPlayer

    val isInsideBlock: Boolean get() = !level().getBlockState(blockPosition()).getCollisionShape(level(), blockPosition()).isEmpty

    private fun applyStateFlags(state: MaskState) {
        noPhysics = state == MaskState.CHASING || state == MaskState.RETURNING
        setGlowingTag(state == MaskState.CHASING)
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(TYPE, 0)
        builder.define(MODE, 0)
        builder.define(STATE, 0)
        builder.define(FROZEN, false)
    }

    override fun onSyncedDataUpdated(data: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(data)
        if (data == STATE) applyStateFlags(state)
    }

    override fun registerGoals() {
        goalSelector.addGoal(0, MaskChaseGoal(this))
        goalSelector.addGoal(0, MaskReturnGoal(this))
        goalSelector.addGoal(1, MaskEchoGoal(this))
        goalSelector.addGoal(2, MaskGuardGoal(this))
        goalSelector.addGoal(2, MaskPatrolGoal(this))
        goalSelector.addGoal(2, MaskWanderGoal(this))
    }

    override fun tick() {
        super.tick()

        if (level().isClientSide) {
            idleState.animateWhen(state != MaskState.CHASING && state != MaskState.STUNNED, tickCount)
            spottedState.animateWhen(state == MaskState.CHASING && state != MaskState.STUNNED, tickCount)
            stunnedState.animateWhen(state == MaskState.STUNNED, tickCount)
        } else {
            VibrationSystem.Ticker.tick(level(), vibrations, listenerUser)
        }
    }

    override fun customServerAiStep() {
        val level = level() as ServerLevel

        if (!initialized) {
            initialized = true
            if (maskType == MaskType.BLACK && targetId == null) return discard()
            if (maskType == MaskType.RANDOM && ModDimensions.isPocketDimension(level)) maskType = MaskType.random(random)
        }

        if (frozen) return

        stateTimer++

        when (state) {
            MaskState.IDLE -> {
                if (--chimeTimer <= 0) {
                    chimeTimer = 200 + random.nextInt(401)
                    playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1f, 0.6f)
                }
                detect(level)?.let(::alert)
            }
            MaskState.STUNNED -> {
                level.getEntitiesOfClass(ServerPlayer::class.java, boundingBox.inflate(0.0, 0.5, 0.0)) { it.fallDistance > 0 && it.y > y + bbHeight * 0.5 }.firstOrNull()?.let(::shatter)
                if (!isRemoved && stateTimer >= STUN_DURATION) state = MaskState.CHASING
            }
            MaskState.CHASING -> chaseTick(level)
            MaskState.RETURNING -> detect(level)?.let(::alert)
        }
    }

    private fun chaseTick(level: ServerLevel) {
        val target = chaseTarget

        if (target == null || !canSense(target) || !inSamePocket(target) || target.distanceTo(this) > LOSE_TARGET_RADIUS) {
            if (maskType == MaskType.BLACK) return discard()
            detect(level)?.let(::chase) ?: returnHome()
            return
        }

        if (boundingBox.intersects(target.boundingBox) && MaskEvents.ALLOW_CATCH.invoker().invoke(this, target)) return catchPlayer(target)

        if (isInsideBlock && tickCount % 5 == 0) {
            val blockState = level.getBlockState(blockPosition())
            level.sendParticles(BlockParticleOption(ParticleTypes.BLOCK, blockState), x, y + bbHeight * 0.5, z, 6, 0.3, 0.3, 0.3, 0.0)
            playSound(blockState.soundType.hitSound, 0.6f, 0.6f)
        }
    }

    fun detect(level: ServerLevel): ServerPlayer? = if (!maskType.isSighted) null else level.players().firstOrNull { player ->
        canSense(player) && inSight(player.eyePosition, MaskEvents.DETECTION_RANGE.invoker().invoke(this, player, maskType.detectionRange)) && hasLineOfSight(player)
    }

    fun inSight(point: Vec3, range: Double): Boolean {
        val offset = point.subtract(eyePosition)
        if (offset.lengthSqr() > range * range) return false

        val flat = Vec3(offset.x, 0.0, offset.z)
        return flat.lengthSqr() < 1.0E-4 || Vec3.directionFromRotation(0f, yRot).dot(flat.normalize()) >= cos(Math.toRadians(maskType.sightHalfAngle))
    }

    private fun canSense(player: ServerPlayer) = EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player) && MaskEvents.ALLOW_DETECT.invoker().invoke(this, player)

    private fun inSamePocket(player: ServerPlayer) = player.level() == level() && pocketId(level(), player.blockPosition()) == pocketId(level(), blockPosition())

    fun alert(player: ServerPlayer) {
        if (maskType == MaskType.BLACK) return

        val level = level() as ServerLevel
        level.playSound(null, blockPosition(), SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.HOSTILE, 4f, 0.5f)

        val pocket = PocketRegistry.instance.peekPocketDirectory(level.dimension())?.getPocketAt(blockPosition())
        val masks = if (pocket != null) level.getEntitiesOfClass(MaskEntity::class.java, AABB.of(pocket.box)) else listOf(this)
        masks.filter { it.maskType != MaskType.BLACK && !it.frozen }.forEach { it.chase(player) }

        if (level.gameRules.getBoolean(ModGameRules.DUNGEON_REAPER)) player.addEffect(ModMobEffects.CHASED.createInstance(-1, 0, false, true, true))

        MaskEvents.ALERT.invoker().invoke(this, player)
    }

    fun chase(player: ServerPlayer) {
        targetId = player.uuid
        state = MaskState.CHASING
    }

    private fun returnHome() {
        targetId = null
        state = if (home == null) MaskState.IDLE else MaskState.RETURNING
    }

    private fun stun(player: ServerPlayer) {
        targetId = player.uuid
        state = MaskState.STUNNED
        (level() as ServerLevel).sendParticles(ParticleTypes.CRIT, x, y + bbHeight, z, 8, 0.3, 0.1, 0.3, 0.0)
        MaskEvents.STUN.invoker().invoke(this, player)
    }

    private fun shatter(player: ServerPlayer) {
        spawnAtLocation(ModItems.MASK_SHARD)
        player.addEffect(ModMobEffects.CHASED.createInstance(-1, 0, false, true, true))
        playSound(SoundEvents.GLASS_BREAK, 1f, 0.8f)
        MaskEvents.SHATTER.invoker().invoke(this, player)
        discard()
    }

    fun catchPlayer(player: ServerPlayer) {
        if (maskType == MaskType.BLACK) {
            LimboTarget.teleportToLimbo(player)
        } else {
            MaskWearerEffects.equip(player, maskType)
            platform.sendPacket(player, MaskCatchAnimS2CPacket(maskType))
        }

        MaskEvents.CATCH.invoker().invoke(this, player)
        discard()
    }

    private fun dodge() {
        repeat(8) {
            val offset = Vec3(random.nextGaussian() * 3, random.nextGaussian(), random.nextGaussian() * 3)
            if (level().noCollision(this, boundingBox.move(offset))) {
                teleportTo(x + offset.x, y + offset.y, z + offset.z)
                return
            }
        }
    }

    fun flyToward(target: Vec3, speed: Double): Boolean {
        val offset = target.subtract(position())
        val distance = offset.length()

        if (distance <= speed) {
            deltaMovement = offset
            return true
        }

        deltaMovement = offset.scale(speed / distance)
        face(offset)
        return false
    }

    fun face(direction: Vec3) {
        if (direction.horizontalDistanceSqr() < 1.0E-6) return
        yRot = (Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG).toFloat() - 90f
        yBodyRot = yRot
        yHeadRot = yRot
    }

    fun breakPlacedAhead() {
        val level = level() as ServerLevel
        val ahead = BlockPos.containing(position().add(Vec3.directionFromRotation(0f, yRot)))
        listOf(ahead, ahead.above()).forEach { if (PlacedContent.isPlaced(level, it) && !level.getBlockState(it).isAir) breakPlaced(level, it) }
    }

    fun breakPlaced(level: ServerLevel, pos: BlockPos) {
        level.destroyBlock(pos, true, this)
        PlacedContent.tracker.onRemoved(level, pos)
    }

    override fun travel(input: Vec3) {
        if (level().isClientSide) return
        move(MoverType.SELF, deltaMovement)
        deltaMovement = Vec3.ZERO
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if (source.`is`(DamageTypeTags.BYPASSES_INVULNERABILITY)) return super.hurt(source, amount)
        if (level().isClientSide) return false

        if (source.`is`(DamageTypes.FALLING_ANVIL) && maskType != MaskType.BLACK) {
            remove(RemovalReason.KILLED)
            return false
        }

        val player = source.entity as? ServerPlayer ?: return false

        if (player.isCreative && player.mainHandItem.`is`(ModItems.MASK_WAND)) {
            if (player.isShiftKeyDown) toggleFrozen() else cycleType()
            return false
        }

        if (frozen) return false

        if (maskType == MaskType.FORESIGHT && source.directEntity is Projectile && state != MaskState.STUNNED) {
            dodge()
            alert(player)
            return false
        }

        if (state == MaskState.CHASING && source.directEntity == player && MaskEvents.ALLOW_CATCH.invoker().invoke(this, player)) catchPlayer(player)
        else if (state == MaskState.IDLE && mode == MaskMode.PATROL && maskType != MaskType.BLACK) stun(player)

        return false
    }

    private fun toggleFrozen() {
        frozen = !frozen
        if (!frozen) return

        targetId = null
        state = MaskState.IDLE
        home?.let { teleportTo(it.x + 0.5, it.y + 1.0, it.z + 0.5) }
    }

    private fun cycleType() {
        val types = MaskType.entries - MaskType.BLACK
        maskType = types[(types.indexOf(maskType) + 1) % types.size]
    }

    override fun getVibrationData() = vibrations

    override fun getVibrationUser(): VibrationSystem.User = listenerUser

    override fun updateDynamicGameEventListener(listenerConsumer: BiConsumer<DynamicGameEventListener<*>, ServerLevel>) {
        (level() as? ServerLevel)?.let { listenerConsumer.accept(gameEventListener, it) }
    }

    override fun addAdditionalSaveData(nbt: CompoundTag) {
        super.addAdditionalSaveData(nbt)
        nbt.putString("mask_type", maskType.getSerializedName())
        nbt.putString("mode", mode.getSerializedName())
        home?.let { nbt.putLong("home_pos", it.asLong()) }
        nbt.putLongArray("waypoints", waypoints.map { it.asLong() }.toLongArray())
        nbt.putInt("waypoint_index", waypointIndex)
        nbt.putBoolean("waypoint_reverse", waypointReverse)
        nbt.putBoolean("frozen", frozen)
    }

    override fun readAdditionalSaveData(nbt: CompoundTag) {
        super.readAdditionalSaveData(nbt)
        maskType = MaskType.CODEC.byName(nbt.getString("mask_type"), MaskType.CYCLOP)
        mode = MaskMode.CODEC.byName(nbt.getString("mode"), MaskMode.GUARD)
        home = if (nbt.contains("home_pos")) BlockPos.of(nbt.getLong("home_pos")) else null
        waypoints = nbt.getLongArray("waypoints").map(BlockPos::of)
        waypointIndex = nbt.getInt("waypoint_index")
        waypointReverse = nbt.getBoolean("waypoint_reverse")
        frozen = nbt.getBoolean("frozen")
    }

    override fun isPushable() = false

    override fun causeFallDamage(fallDistance: Float, multiplier: Float, source: DamageSource) = false

    override fun removeWhenFarAway(distanceToClosestPlayer: Double) = false

    override fun requiresCustomPersistence() = true

    companion object {
        const val CHASE_SPEED = 4.6 / 20
        const val CHASE_SPEED_IN_BLOCK_MULT = 0.5
        const val IDLE_SPEED = 1.5 / 20
        const val LOSE_TARGET_RADIUS = 50f
        const val STUN_DURATION = 60
        const val PATROL_WAYPOINT_PAUSE = 60
        const val GUARD_WALL_AVOID_DIST = 3
        const val WANDER_RADIUS = 8.0
        const val BLOCK_DISTURB_RADIUS = 5.0
        const val CHASED_SPAWN_DELAY = 200
        const val MAX_WAYPOINTS = 10

        private val TYPE = SynchedEntityData.defineId(MaskEntity::class.java, EntityDataSerializers.BYTE)
        private val MODE = SynchedEntityData.defineId(MaskEntity::class.java, EntityDataSerializers.BYTE)
        private val STATE = SynchedEntityData.defineId(MaskEntity::class.java, EntityDataSerializers.BYTE)
        private val FROZEN = SynchedEntityData.defineId(MaskEntity::class.java, EntityDataSerializers.BOOLEAN)

        val CHASED: Holder<MobEffect> get() = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(ModMobEffects.CHASED)

        @JvmStatic
        fun createAttributes(): AttributeSupplier.Builder = Mob.createMobAttributes()

        fun pocketId(level: Level, pos: BlockPos) = PocketRegistry.instance.peekPocketDirectory(level.dimension())?.getPocketAt(pos)?.id

        fun registerEvents() {
            platform.onAfterBlockPlace { level, pos, _, placer -> (placer as? ServerPlayer)?.let { disturb(level, pos, it) } }
            platform.onAfterBlockBreak { level, pos, _, player -> (player as? ServerPlayer)?.let { disturb(level, pos, it) } }

            PlayerTeleportEvents.AFTER.register { player, level, _ ->
                DataValues.PENDING_REAPER.remove(player)

                if (ModDimensions.isRootDimension(level)) player.removeEffect(CHASED)
                else if (level.dimension() == ModDimensions.DUNGEON && player.getEffect(CHASED)?.amplifier == 0) DataValues.PENDING_REAPER.set(player, PendingReaper(player.blockPosition(), CHASED_SPAWN_DELAY))
            }

            platform.onServerLevelTick { level -> level.players().forEach(::tickReaper) }
        }

        private fun disturb(level: ServerLevel, pos: BlockPos, player: ServerPlayer) {
            val center = Vec3.atCenterOf(pos)
            level.getEntitiesOfClass(MaskEntity::class.java, AABB(pos).inflate(BLOCK_DISTURB_RADIUS)) {
                it.state == MaskState.IDLE && !it.frozen && it.maskType.isSighted && it.position().closerThan(center, BLOCK_DISTURB_RADIUS) && it.canSense(player)
            }.firstOrNull()?.alert(player)
        }

        private fun tickReaper(player: ServerPlayer) {
            val pending = DataValues.PENDING_REAPER.get(player) ?: return

            if (pending.ticks > 0) return DataValues.PENDING_REAPER.set(player, pending.copy(ticks = pending.ticks - 1))

            DataValues.PENDING_REAPER.remove(player)
            val level = player.serverLevel()
            val reaper = MaskEntity(ModEntityTypes.MASK, level)
            reaper.maskType = MaskType.BLACK
            reaper.moveTo(Vec3.atBottomCenterOf(pending.entry))
            reaper.chase(player)
            level.addFreshEntity(reaper)
        }
    }
}
