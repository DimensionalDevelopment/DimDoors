package org.dimdev.dimdoors.rift.targets

import net.minecraft.core.Rotations
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimcore.api.util.EntityUtils.chat
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.rift.target.EntityTarget
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.pockets.PocketGenerationHelper
import org.dimdev.dimdoors.pockets.generatePocket
import org.dimdev.dimdoors.rift.registry.DialingAddress
import org.dimdev.dimdoors.rift.registry.DialingRegistry
import org.dimdev.dimdoors.rift.registry.PlayerTrackerPointer
import org.dimdev.dimdoors.rift.registry.Vertex
import org.dimdev.dimdoors.world.pocket.VirtualLocation
import org.dimdev.dimdoors.world.pocket.type.DialingPocket
import java.util.*

interface DialingTarget : PlayerTrackingEntranceTarget<DialingAddress, DialingPocket, DialingRegistry> {
    var address: DialingAddress

    override fun processEntity(
        owner: Vertex,
        pocket: DialingPocket,
        target: EntityTarget,
        entity: Entity,
        uuid: UUID,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3
    ): Boolean {
        val received = target.receiveEntity(owner, entity, relativePos, relativeAngle, relativeVelocity, null)
        if (received) {
            (this as? Rift<*>)?.takeIf { !it.isRegistered }?.let {
                DimensionalDoors.LOGGER.warn(
                    "Dialing source at {} was not registered before setting return exit; registering now.",
                    this.location
                )
                it.register()
            }

            subsystem.setPlayerAddress(uuid, this.address)
            subsystem.setRift(uuid, PlayerTrackerPointer.Variant.Exit, owner)
        }
        return received
    }

    override val subsystem: DialingRegistry get() = DialingRegistry.instance

    override fun getKey(uuid: UUID?): DialingAddress? = this.address

    override fun createPocket(virtualLocation: VirtualLocation): DialingPocket? {
        val pocket = PocketGenerationHelper.DIALING.generatePocket(
            VirtualLocation(virtualLocation.world, virtualLocation.x, virtualLocation.z, -1).also { it.castOrNull<DialingTarget>()?.also { it.address = address } })

        if (pocket is DialingPocket) {
            pocket.address = address
            return pocket
        }
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
