package org.dimdev.dimdoors.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Rotations
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.api.util.math.inverse
import org.dimdev.dimdoors.api.util.math.transform
import org.dimdev.dimdoors.api.util.math.transformDirection
import org.joml.Matrix4dc

interface CoordinateTransformerBlock {
    fun transformation(state: BlockState, pos: BlockPos): Matrix4dc

    fun rotator(state: BlockState, pos: BlockPos): Matrix4dc

    fun isExitFlipped(): Boolean = false

    fun transformTo(transformation: Matrix4dc, vector: Vec3): Vec3 = transformation.transform(vector)
    fun transformOut(transformation: Matrix4dc, vector: Vec3): Vec3 = transformation.inverse().transform(vector)
    fun rotateTo(rotator: Matrix4dc, angle: Rotations): Rotations = rotator.transform(angle)
    fun rotateTo(rotator: Matrix4dc, vector: Vec3): Vec3 = rotator.transformDirection(vector)
    fun rotateOut(rotator: Matrix4dc, angle: Rotations): Rotations = rotator.inverse().transform(angle)
    fun rotateOut(rotator: Matrix4dc, vector: Vec3): Vec3 = rotator.inverse().transformDirection(vector)
}