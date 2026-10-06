package org.dimdev.dimdoors.api.rift.target

import net.minecraft.core.Rotations
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.registry.Vertex

fun interface EntityTarget : Target {
    fun receiveEntity(
        owner: Vertex,
        entity: Entity,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3,
        location: Location?
    ): Boolean
}
