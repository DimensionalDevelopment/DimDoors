package org.dimdev.dimdoors.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.portal.DimensionTransition
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.castOrNull
import org.dimdev.dimdoors.item.RaycastHelper.hitsDetachedRift
import org.dimdev.dimdoors.item.RaycastHelper.projectileCast

class FarShotEnderPearlEntity : ThrowableItemProjectile {
    private val setTargetOnFire: Boolean
    private val punchLevel: Int

    constructor(entityType: EntityType<out FarShotEnderPearlEntity>, level: Level) : super(entityType, level) {
        this.setTargetOnFire = false
        this.punchLevel = 0
    }

    @JvmOverloads
    constructor(level: Level, shooter: LivingEntity, hasFlaming: Boolean = false, punchLevel: Int = 0) : super(
        ModEntityTypes.FARSHOT_ENDER_PEARL.value(), shooter, level
    ) {
        this.setTargetOnFire = hasFlaming
        this.punchLevel = punchLevel
    }

    override fun getDefaultItem(): Item {
        return Items.ENDER_PEARL
    }

    override fun onHitEntity(result: EntityHitResult) {
        super.onHitEntity(result)

        val target = result.entity
        target.hurt(this.damageSources().thrown(this, this.owner), 0.0f) // vanilla parity: aggro the target

        val owner = this.owner?.takeIf { owner == target } ?: return
        val level = this.level().castOrNull<ServerLevel>()?.takeIf { owner.level() !== it } ?: return

        val ownerPos = owner.position()
        val targetPos = target.position()
        val ownerYRot = owner.yRot
        val ownerXRot = owner.xRot
        val targetYRot = target.yRot
        val targetXRot = target.xRot

        owner.unRide()
        target.unRide()

        for (pos in arrayOf<Vec3>(ownerPos, targetPos)) {
            level.sendParticles(
                ParticleTypes.PORTAL,
                pos.x,
                pos.y + 1.0,
                pos.z,
                48,
                0.35,
                0.75,
                0.35,
                0.35
            )
            level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f)
        }

        owner.resetFallDistance()
        if (owner is ServerPlayer) {
            owner.connection.teleport(targetPos.x, targetPos.y, targetPos.z, ownerYRot, ownerXRot)
        } else {
            owner.teleportTo(targetPos.x, targetPos.y, targetPos.z)
        }

        if (setTargetOnFire) target.igniteForSeconds(5f)
        target.resetFallDistance()
        if (target is ServerPlayer) {
            target.connection.teleport(ownerPos.x, ownerPos.y, ownerPos.z, targetYRot, targetXRot)
        } else {
            target.teleportTo(ownerPos.x, ownerPos.y, ownerPos.z)
        }

        if (punchLevel > 0) {
            target.addDeltaMovement(this.deltaMovement.normalize().scale(0.8 * punchLevel))
        }

        this.discard()
    }

    override fun onHit(result: HitResult) {
        super.onHit(result)

        repeat((0..31).count()) {
            this.level().addParticle(
                ParticleTypes.PORTAL,
                this.x,
                this.y + this.random.nextDouble() * 2.0,
                this.z,
                this.random.nextGaussian(),
                0.0,
                this.random.nextGaussian()
            )
        }

        val serverlevel = level().castOrNull<ServerLevel>() ?: return

        if (!this.isRemoved) {
            val entity = this.owner?.takeIf { it.isAllowedToTeleportOwner(serverlevel) }

            if (entity != null) {
                if (entity.isPassenger) entity.unRide()

                if (entity is ServerPlayer) {
                    if (entity.connection.isAcceptingMessages) {
                        if (this.random.nextFloat() < 0.05f && serverlevel.gameRules.getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                            val endermite = EntityType.ENDERMITE.create(serverlevel)
                            if (endermite != null) {
                                endermite.moveTo(
                                    entity.x,
                                    entity.y,
                                    entity.z,
                                    entity.yRot,
                                    entity.xRot
                                )
                                serverlevel.addFreshEntity(endermite)
                            }
                        }

                        entity.changeDimension(
                            DimensionTransition(
                                serverlevel,
                                this.position(),
                                entity.deltaMovement,
                                entity.yRot,
                                entity.xRot,
                                DimensionTransition.DO_NOTHING
                            )
                        )
                        entity.resetFallDistance()
                        entity.resetCurrentImpulseContext()
                        entity.hurt(this.damageSources().fall(), 5.0f)
                        this.playSound(serverlevel, this.position())
                    }
                } else {
                    entity.changeDimension(
                        DimensionTransition(
                            serverlevel,
                            this.position(),
                            entity.deltaMovement,
                            entity.yRot,
                            entity.xRot,
                            DimensionTransition.DO_NOTHING
                        )
                    )
                    entity.resetFallDistance()
                    this.playSound(serverlevel, this.position())
                }

                this.discard()
                return
            }

            this.discard()
        }
    }

    override fun tick() {
        if (this.owner?.castOrNull<ServerPlayer>()?.isAlive == true && !this.isAlive && this.level().gameRules.getBoolean(GameRules.RULE_ENDER_PEARLS_VANISH_ON_DEATH)) {
            this.discard()
        } else {
            val hit = projectileCast(this, this::canHitEntity)

            if (hitsDetachedRift(hit, this.level())) {
                this.onHit(hit)
            }

            super.tick()
        }
    }

    private fun playSound(level: Level, pos: Vec3) {
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS)
    }

    override fun canChangeDimensions(oldLevel: Level, newLevel: Level): Boolean {
        if (oldLevel.dimension() === Level.END) {
            return super.canChangeDimensions(oldLevel, newLevel) && (this.owner?.castOrNull<ServerPlayer>()?.seenCredits ?: true)
        }

        return super.canChangeDimensions(oldLevel, newLevel)
    }

    override fun onInsideBlock(state: BlockState) {
        super.onInsideBlock(state)
        if (state.`is`(Blocks.END_GATEWAY)) {
            owner?.castOrNull<ServerPlayer>()?.onInsideBlock(state)
        }
    }

    companion object {
        fun Entity.isAllowedToTeleportOwner(level: Level) = if (this.level().dimension() !== level.dimension()) {
            this.canUsePortal(true)
        } else {
            this.isAlive && this.castOrNull<LivingEntity>()?.isAlive ?: true
        }
    }
}
