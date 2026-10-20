package org.dimdev.dimdoors.entity.mask

import net.minecraft.util.Mth
import net.minecraft.world.entity.*
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class MaskEntity(entityType: EntityType<out MaskEntity>, level: Level) : Mob(entityType, level) {
    @JvmField
    val idleState: AnimationState = AnimationState()
    @JvmField
    val spottedState: AnimationState = AnimationState()


    var mode: MaskMode
        get() {
            val id = entityData.get<Byte>(MODE)
            val values = MaskMode.entries.toTypedArray()
            return values[Mth.clamp(id.toInt(), 0, values.size - 1)]
        }
        set(mode) {
            entityData.set(MODE, mode.ordinal.toByte())
            applyModeFlags(mode)
        }

    private fun applyModeFlags(mode: MaskMode?) {
        setGlowingTag(mode == MaskMode.CHASE)
        noPhysics = mode == MaskMode.CHASE
        isInvulnerable = mode == MaskMode.CHASE || mode == MaskMode.STUNNED
    }

}


