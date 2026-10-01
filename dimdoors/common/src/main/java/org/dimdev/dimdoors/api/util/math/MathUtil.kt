package org.dimdev.dimdoors.api.util.math

import net.minecraft.core.Direction
import net.minecraft.core.Rotations
import net.minecraft.core.Vec3i
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import kotlin.math.asin
import kotlin.math.atan2

object MathUtil {
    fun <T> weightedRandom(weights: MutableMap<T, Float>): T? {
        if (weights.isEmpty()) return null
        var totalWeight = 0
        for (weight in weights.values) {
            totalWeight = (totalWeight + weight).toInt()
        }
        val random = RandomSource.create()
        var f = random.nextFloat() * totalWeight
        for (e in weights.entries) {
            f -= e.value
            if (f < 0) return e.key
        }
        return null
    }

    @JvmStatic
    fun eulerAngle(direction: Vec3, upwards: Vec3): Rotations {
        var upwards = upwards
        val pitch = pitch(direction)
        val yaw = yaw(direction)
        upwards = Rotations(pitch, yaw, 0f).toMatrix().inverse().transform(upwards)
        val roll = Math.toDegrees(-atan2(upwards.x, upwards.y)).toFloat()

        return Rotations(pitch, yaw, roll)
    }

    @JvmStatic
    fun entityEulerAngle(entity: Entity) = Rotations(entity.xRot, entity.yRot, 0f)

    fun yaw(vector: Vec3): Float {
        return Math.toDegrees(-atan2(vector.x, vector.z)).toFloat()
    }

    fun pitch(vector: Vec3): Float {
        return Math.toDegrees(asin(-vector.y)).toFloat()
    }

    val Direction.eulerAngle  get() = directionEulerAngle(this)

    @JvmStatic
    fun directionEulerAngle(direction: Direction) = when (direction) {
        Direction.DOWN -> EulerAngleDirection.DOWN.angle
        Direction.UP -> EulerAngleDirection.UP.angle
        Direction.NORTH -> EulerAngleDirection.NORTH.angle
        Direction.SOUTH -> EulerAngleDirection.SOUTH.angle
        Direction.WEST -> EulerAngleDirection.WEST.angle
        else -> EulerAngleDirection.EAST.angle
    }

    @JvmStatic
    fun between(value: Int, min: Int, max: Int) = value in min..max

    enum class EulerAngleDirection(val angle: Rotations) {
        DOWN(Rotations(90f, 0f, 0f)),
        UP(Rotations(-90f, 0f, 0f)),
        NORTH(Rotations(0f, -180f, 0f)),
        SOUTH(Rotations(0f, 0f, 0f)),
        WEST(Rotations(0f, 90f, 0f)),
        EAST(Rotations(0f, -90f, 0f));
    }

    operator fun Vec3i.times(scale: Double) = Vec3.atLowerCornerOf(this).scale(scale)

    operator fun Vec3.plus(vec: Vec3) = this.add(vec)
}