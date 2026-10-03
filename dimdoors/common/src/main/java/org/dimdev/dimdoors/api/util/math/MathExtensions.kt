package org.dimdev.dimdoors.api.util.math

import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.world.level.border.WorldBorder
import net.minecraft.world.phys.Vec3
import kotlin.math.abs

val Direction.eulerAngle get() = MathUtil.directionEulerAngle(this)

operator fun Vec3i.times(scale: Double): Vec3 = Vec3.atLowerCornerOf(this).scale(scale)

operator fun Vec3.plus(vec: Vec3): Vec3 = this.add(vec)

fun WorldBorder.clamp(original: Vec3): Vec3 {

    var newX = original.x
    var newZ = original.z
    val size = size - 1
    val northBound = minZ + 1
    val southBound = maxZ - 1
    val westBound = minX + 1
    val eastBound = maxX - 1

    if (newZ < northBound) { newZ = northBound + abs(newZ % size) + 1 } else if (newZ > southBound) { newZ = southBound - abs(newZ % size) - 1 }
    if (newX < westBound) newX = westBound + abs(newX % size) + 1 else if (newX > eastBound) newX = eastBound - abs(newX % size) - 1

    return Vec3(newX, original.y, newZ)
}
