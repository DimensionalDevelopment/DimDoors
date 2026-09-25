package org.dimdev.dimdoors.util

import net.minecraft.core.Rotations
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object RotationUtil {
    private val DEG_TO_RAD = Math.PI / 180.0
    private val RAD_TO_DEG = 180.0 / Math.PI
    val ZERO: Rotations = Rotations(0f, 0f, 0f)

    fun directionFromRot(rot: Rotations): Vec3 {
        return directionFromRot(rot.getX(), rot.getY())
    }

    fun directionFromRot(pitch: Float, yaw: Float): Vec3 {
        val pitchRad = pitch * DEG_TO_RAD
        val yawRad = yaw * DEG_TO_RAD
        val cosPitch = cos(pitchRad)

        return Vec3(
            (-sin(yawRad) * cosPitch).toFloat().toDouble(),
            (-sin(pitchRad).toFloat()).toDouble(),
            (cos(yawRad) * cosPitch).toFloat().toDouble()
        )
    }

    fun rotFromDirection(direction: Vec3, roll: Float): Rotations {
        if (direction.lengthSqr() < 1.0E-12) {
            return Rotations(0.0f, 0.0f, roll)
        }

        val d = direction.normalize()
        val horizontal = sqrt(d.x * d.x + d.z * d.z)

        val pitch = (atan2(-d.y, horizontal) * RAD_TO_DEG).toFloat()
        val yaw = (atan2(-d.x, d.z) * RAD_TO_DEG).toFloat()

        return Rotations(
            Mth.clamp(pitch, -90.0f, 90.0f),
            Mth.wrapDegrees(yaw),
            roll
        )
    }

    fun rotFromDirection(direction: Vec3, original: Rotations): Rotations {
        return rotFromDirection(direction, original.getZ())
    }
}