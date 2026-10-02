package org.dimdev.dimdoors.entity.ai

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.player.Player
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.DimensionalDoors.Companion.config
import org.dimdev.dimdoors.api.util.TeleportUtil
import org.dimdev.dimdoors.entity.MonolithEntity
import org.dimdev.dimdoors.entity.stat.ModStats
import org.dimdev.dimdoors.network.ServerPacketHandler
import org.dimdev.dimdoors.network.packet.s2c.MonolithAggroParticlesPacket
import org.dimdev.dimdoors.network.packet.s2c.MonolithTeleportParticlesPacket
import org.dimdev.dimdoors.sound.ModSoundEvents
import org.dimdev.dimdoors.tag.ModItemTags
import org.dimdev.dimdoors.world.ModDimensions
import java.util.*

open class MonolithAggroGoal(protected val mob: MonolithEntity, protected val range: Float) : Goal() {
    protected var target: Player? = null
    protected val targetPredicate: TargetingConditions

    init {
        this.flags = EnumSet.of(Flag.LOOK)
        this.targetPredicate = TargetingConditions.forNonCombat().range(this.range.toDouble()).ignoreInvisibilityTesting().selector(EntitySelector.NO_CREATIVE_OR_SPECTATOR::test)
    }

    private fun findTarget(): Player? {
        val playerEntity = this.mob.level().getNearestPlayer(this.targetPredicate, this.mob, this.mob.x, this.mob.eyeY, this.mob.z)
        return if (playerEntity != null && this.mob.hasLineOfSight(playerEntity) && playerEntity.distanceTo(this.mob) < 50) playerEntity else null
    }

    override fun canUse(): Boolean {
        return (this.findTarget().also { this.target = it }) != null && this.target!!.distanceTo(this.mob) <= 50
    }

    override fun canContinueToUse(): Boolean {
        return (this.findTarget().also { this.target = it }) != null && this.target!!.distanceTo(this.mob) <= 50
    }

    override fun start() {
    }

    override fun stop() {
        this.target = null
        this.mob.aggro = 0
    }

    override fun tick() {
        if (this.target != null && this.target!!.distanceTo(this.mob) > 70 && this.mob.aggro == 0) {
            this.stop()
            return
        }

        if (this.target != null) {
            val slots = EquipmentSlot.entries.filter { a: EquipmentSlot -> target!!.getItemBySlot(a).`is`(ModItemTags.LIMBO_GAZE_DEFYING) }.toList()

            if (!slots.isEmpty()) {
                val random = RandomSource.create()

                val i = random.nextInt((64 + 16 * (slots.size / 4)))
                if (this.target is ServerPlayer) {
                    if (i < 4) {
                        val slot = random.nextIntBetweenInclusive(0, slots.size - 1)

                        val equip = slots[slot]

                        val item = this.target!!.getItemBySlot(equip)
                        val serverPlayer = target as ServerPlayer
                        item.hurtAndBreak(i, serverPlayer.serverLevel(), serverPlayer) {}
                    }

                    this.mob.updateAggroLevel(this.target, false)
                }

                return
            }
        }

        val visibility = this.target != null
        this.mob.updateAggroLevel(this.target, visibility)

        // Change orientation and face a player if one is in range
        if (this.target != null) {
            this.mob.facePlayer(this.target!!)
            if (this.mob.isDangerous) {
                // Play sounds on the server side, if the player isn't in Limbo.
                // Limbo is excluded to avoid drowning out its background music.
                // Also, since it's a large open area with many Monoliths, some
                // of the sounds that would usually play for a moment would
                // keep playing constantly and would get very annoying.
                this.mob.playSounds(this.target!!.position())
                ServerPacketHandler.sendPacket(this.target as ServerPlayer, MonolithAggroParticlesPacket(this.mob.aggro))
            }

            // Teleport the target player if various conditions are met
            if (this.mob.aggro >= MonolithEntity.MAX_AGGRO && config.monolithsConfig.monolithTeleportation && !this.target!!.isCreative && this.mob.isDangerous) {
                this.mob.aggro = 0
                TeleportUtil.teleport(
                    this.target!!,
                    DimensionalDoors.getWorld(ModDimensions.LIMBO)!!,
                    this.target!!.position().add(0.0, 256.0, 0.0),
                    this.target!!.visualRotationYInDegrees
                )
                this.target!!.level().playSound(
                    null,
                    BlockPos(
                        Vec3i(
                            this.target!!.position().x.toInt(),
                            this.target!!.position().y.toInt(),
                            this.target!!.position().z.toInt()
                        )
                    ),
                    ModSoundEvents.CRACK,
                    SoundSource.HOSTILE,
                    13f,
                    1f
                )
                this.target!!.awardStat(ModStats.TIMES_TELEPORTED_BY_MONOLITH)
                ServerPacketHandler.sendPacket(this.target as ServerPlayer, MonolithTeleportParticlesPacket)
            }
        }
    }
}
