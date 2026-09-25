@file:JvmName("Transforms")
package org.dimdev.dimdoors.api.util.math

import net.minecraft.core.Rotations
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.util.RotationUtil
import org.joml.Matrix4d
import org.joml.Matrix4dc
import org.joml.Vector3d

private val FORWARD: Vector3d get() = Vector3d(0.0, 0.0, 1.0)
private val UP: Vector3d get() = Vector3d(0.0, 1.0, 0.0)

fun Matrix4d.translateLocal(v: Vec3): Matrix4d = translateLocal(v.x, v.y, v.z)

fun Matrix4d.inverseTranslateLocal(v: Vec3): Matrix4d = translateLocal(-v.x, -v.y, -v.z)

fun Matrix4d.rotateLocal(angle: Rotations): Matrix4d = this
    .rotateLocalZ(Math.toRadians(angle.z.toDouble()))
    .rotateLocalX(Math.toRadians(angle.x.toDouble()))
    .rotateLocalY(Math.toRadians(-angle.y.toDouble()))

fun Matrix4d.inverseRotateLocal(angle: Rotations): Matrix4d = this
    .rotateLocalZ(-Math.toRadians(angle.z.toDouble()))
    .rotateLocalX(-Math.toRadians(angle.x.toDouble()))
    .rotateLocalY(Math.toRadians(angle.y.toDouble()))

fun Rotations.toMatrix(): Matrix4d = Matrix4d().rotateLocal(this)

fun Matrix4dc.inverse(): Matrix4d = invertAffine(Matrix4d())

fun Matrix4dc.transform(v: Vec3): Vec3 = transformPosition(Vector3d(v.x, v.y, v.z)).toVec3()

fun Matrix4dc.transformDirection(v: Vec3): Vec3 = transformDirection(Vector3d(v.x, v.y, v.z)).toVec3()

fun Matrix4dc.transform(angle: Rotations): Rotations {
    val basis = angle.toMatrix()
    val forward = transformDirection(basis.transformDirection(FORWARD))
    val up = transformDirection(basis.transformDirection(UP))

    if (forward.lengthSquared() < 1.0E-12) return angle
    forward.normalize()
    up.fma(-up.dot(forward), forward)

    if (up.lengthSquared() < 1.0E-12) return RotationUtil.rotFromDirection(forward.toVec3(), angle.z)
    return MathUtil.eulerAngle(forward.toVec3(), up.normalize().toVec3())
}

fun Vector3d.toVec3(): Vec3 = Vec3(x, y, z)