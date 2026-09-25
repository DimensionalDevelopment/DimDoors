package org.dimdev.dimdoors.api.rift.target

import net.minecraft.core.Rotations
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.api.util.Location

fun interface EntityTarget : Target {
    fun receiveEntity(
        entity: Entity,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3,
        location: Location?
    ): Boolean
}
