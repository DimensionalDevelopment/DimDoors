package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import net.minecraft.core.Rotations
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.api.rift.target.EntityTarget
import org.dimdev.dimdoors.pockets.PocketGenerator
import org.dimdev.dimdoors.rift.registry.PlayerTrackerPointer
import org.dimdev.dimdoors.rift.registry.Vertex
import org.dimdev.dimdoors.rift.registry.HubRegistry
import org.dimdev.dimdoors.world.pocket.VirtualLocation
import org.dimdev.dimdoors.world.pocket.type.HubPocket
import java.util.*

object HubPocketTarget : VirtualTarget<HubPocketTarget>(), PlayerTrackingEntranceTarget<HubRegistry.HubKey, HubPocket, HubRegistry> {

    override fun processEntity(
        owner: Vertex,
        pocket: HubPocket,
        target: EntityTarget,
        entity: Entity,
        uuid: UUID,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3
    ): Boolean {
        val received = target.receiveEntity(owner, entity, relativePos, relativeAngle, relativeVelocity, null)
        if (received) {
            subsystem.setRift(uuid, PlayerTrackerPointer.Variant.Exit, owner)
        }
        return received
    }

    override val type get() = VirtualTargets.HUB

    override fun getColor(owner: Vertex) = HubPocketExitTarget.color

    override fun copy(): HubPocketTarget = this

    override fun equals(other: Any?) = this === other

    override fun hashCode() = System.identityHashCode(this)

    override val subsystem: HubRegistry get() = HubRegistry.instance

    override val pocketClass = HubPocket::class.java

    override fun getKey(uuid: UUID?): HubRegistry.HubKey = HubRegistry.HubKey

    override fun createPocket(virtualLocation: VirtualLocation): HubPocket? {
        val pocket = PocketGenerator.generateHubPocket(
            VirtualLocation(
                virtualLocation.world,
                virtualLocation.x,
                virtualLocation.z,
                -1
            )
        )

        return pocket?.castOrNull<HubPocket>()
    }

    val codec = MapCodec.unit(HubPocketTarget)
}
