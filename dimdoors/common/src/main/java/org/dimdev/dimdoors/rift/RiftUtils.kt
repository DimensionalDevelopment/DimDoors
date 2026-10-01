package org.dimdev.dimdoors.rift

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.TrapDoorBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Half
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.castOrNull
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.block.DimensionalPortalBlock
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.util.Timer
import org.dimdev.dimdoors.util.Timer.Companion.create
import kotlin.math.abs

object RiftUtils {
    val showRiftTimer: Timer = create(40.0, 60.0, 40.0)

    @JvmStatic
    fun triggerRiftCoreHighlight() {
        val highlightMillis = DimensionalDoors.config.graphicsConfig.highlightRiftCoreFor
        if (highlightMillis < 0) {
            showRiftTimer.reset()
            return
        }

        showRiftTimer.trigger()
    }

    fun <T : Rift> registerFunction(rift: T): T {
        rift.register()
        return rift
    }

    fun runIfRiftAt(location: Location, consumer: (Rift) -> Unit) = location.blockEntity?.castOrNull<Rift>()?.run(consumer)

    data class PortalPlane(
        val normal: Vec3,
        val tangentX: Vec3,
        val tangentY: Vec3,
        val origin: Vec3,
        val halfWidth: Double,
        val height: Double
    ) {
        fun isTraversed(level: Level?, previousPos: Vec3, currentPos: Vec3): Boolean {
            val dotCurrent = normal.dot(currentPos.subtract(origin))
            val dotPrevious = normal.dot(previousPos.subtract(origin))

            if (!(dotCurrent <= 0 && dotPrevious >= 0) && !(dotCurrent >= 0 && dotPrevious <= 0) || (dotCurrent == 0.0 && dotPrevious == 0.0)) {
                return false
            }

            val positionChange = currentPos.subtract(previousPos)

            if (positionChange.lengthSqr() == 0.0) {
                return false
            }

            val vecFromPreviousPosToPortalPlane = origin.subtract(previousPos)
            val pointOfIntersection = previousPos.add(
                positionChange.scale(
                    vecFromPreviousPosToPortalPlane.dot(positionChange) /
                            positionChange.dot(positionChange)
                )
            )

            val intersectionRelativeToPortalPlane = pointOfIntersection.subtract(origin)
            val relativeIntersectionU = intersectionRelativeToPortalPlane.dot(tangentX)
            val relativeIntersectionV = intersectionRelativeToPortalPlane.dot(tangentY)

            return abs(relativeIntersectionU) <= halfWidth && relativeIntersectionV >= 0 && relativeIntersectionV <= height
        }

        companion object {
            @JvmStatic
            fun ofDoor(state: BlockState, pos: BlockPos): PortalPlane {
                val normal = Vec3.atLowerCornerOf(
                    state.getValue(DimensionalPortalBlock.FACING).opposite.normal
                )
                val origin = Vec3.atBottomCenterOf(pos).add(normal.scale(0.31))
                val tangentY = Vec3(0.0, 1.0, 0.0)
                val tangentX = normal.cross(tangentY).normalize()
                return PortalPlane(normal, tangentX, tangentY, origin, 0.5, 2.0)
            }

            @JvmStatic
            fun ofTrapdoor(state: BlockState, pos: BlockPos): PortalPlane {
                val isTop = state.getValue<Half?>(TrapDoorBlock.HALF) == Half.TOP
                val normal = if (isTop) Vec3(0.0, -1.0, 0.0) else Vec3(0.0, 1.0, 0.0)
                val origin = Vec3.atBottomCenterOf(pos).add(0.0, (if (isTop) 1 else 0).toDouble(), 0.0)
                val tangentX = Vec3.atLowerCornerOf(state.getValue(DimensionalPortalBlock.FACING).normal)
                val tangentY = normal.cross(tangentX).normalize()
                return PortalPlane(normal, tangentX, tangentY, origin, 0.5, 0.5)
            }
        }
    }
}
