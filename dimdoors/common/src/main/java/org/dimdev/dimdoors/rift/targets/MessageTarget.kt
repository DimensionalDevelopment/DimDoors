package org.dimdev.dimdoors.rift.targets

import net.minecraft.core.Rotations
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.util.EntityUtils.chat
import org.dimdev.dimdoors.api.rift.target.EntityTarget
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.api.util.Location

class MessageTarget(private val forwardTo: Target?, private val message: String, private vararg val messageParams: Any?) :
    EntityTarget {

    constructor(message: String, vararg messageParams: Any?) : this(null, message, *messageParams)

    override fun receiveEntity(
        entity: Entity,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3,
        location: Location?
    ): Boolean {
        chat(entity, Component.translatable(this.message, *this.messageParams))

        if (this.forwardTo != null) {
            this.forwardTo.`as`<EntityTarget>(Targets.ENTITY)!!
                .receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, location)
            return true
        } else {
            return false
        }
    }
}