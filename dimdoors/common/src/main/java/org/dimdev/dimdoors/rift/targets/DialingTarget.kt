package org.dimdev.dimdoors.rift.targets

import net.minecraft.core.Rotations
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.cast
import org.dimdev.dimcore.api.util.EntityUtils.chat
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.rift.target.EntityTarget
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.pockets.PocketGenerator
import org.dimdev.dimdoors.rift.registry.DialingAddress
import org.dimdev.dimdoors.rift.registry.DialingRegistry
import org.dimdev.dimdoors.world.pocket.DialingPocket
import org.dimdev.dimdoors.world.pocket.VirtualLocation
import java.util.*

interface DialingTarget : PlayerTrackingEntranceTarget<DialingAddress?, DialingPocket, DialingRegistry> {
    val address: DialingAddress

    override fun processEntity(
        pocket: DialingPocket,
        target: EntityTarget,
        entity: Entity,
        uuid: UUID,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3
    ): Boolean {
        val received = target.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, null)
        if (received) {
            val sourceRift = this.cast<Rift>().takeIf { it.isRegistered }

            if(sourceRift != null) {
                DimensionalDoors.LOGGER.warn(
                    "Dialing source at {} was not registered before setting return exit; registering now.",
                    this.location
                )
                sourceRift.register()
            }

            subsystem.setPlayerAddress(uuid, this.address)
            subsystem.setExit(uuid, this.location)
        }
        return received
    }

    override val subsystem: DialingRegistry get() = DialingRegistry.instance

    override fun getKey(uuid: UUID?): DialingAddress? = this.address

    override fun createPocket(virtualLocation: VirtualLocation): DialingPocket? {
        val pocket = PocketGenerator.generateDialingPocket(
            VirtualLocation(virtualLocation.world, virtualLocation.x, virtualLocation.z, -1),
            this.address
        )

        if (pocket is DialingPocket) return pocket
        return null
    }

    override val pocketClass get() = DialingPocket::class.java

    override fun onInPocketType(
        entity: Entity?,
        relativePos: Vec3?,
        relativeAngle: Rotations?,
        relativeVelocity: Vec3?,
        location: Location?
    ) {
        chat(entity, Component.translatable("rifts.destinations.dialing.cant_use_dialing_door_in_dialing_pocket"))
    }
}
