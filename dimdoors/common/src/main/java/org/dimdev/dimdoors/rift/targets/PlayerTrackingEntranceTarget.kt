package org.dimdev.dimdoors.rift.targets

import net.minecraft.core.Rotations
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.ext.ownerPlayer
import org.dimdev.dimcore.api.ext.ownerPlayerUuid
import org.dimdev.dimcore.api.util.EntityUtils.chat
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.rift.target.EntityTarget
import org.dimdev.dimdoors.api.rift.target.TargetResolver.entity
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.registry.PlayerTrackerPointer
import org.dimdev.dimdoors.rift.registry.PlayerTrackingSubSystem
import org.dimdev.dimdoors.rift.registry.PocketRegistry.Companion.instance
import org.dimdev.dimdoors.rift.registry.Vertex
import org.dimdev.dimdoors.world.pocket.VirtualLocation
import org.dimdev.dimdoors.world.pocket.type.Pocket
import java.util.*

interface PlayerTrackingEntranceTarget<O, P : Pocket<*, *>, S : PlayerTrackingSubSystem<O, P, S>> : EntityTarget {
    val subsystem: S

    override fun receiveEntity(
        owner: Vertex,
        entity: Entity,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3,
        location: Location?
    ): Boolean {
        val uuid = entity.ownerPlayerUuid ?: return false

        if (this.isCorrectPocketType(owner)) {
            onInPocketType(entity, relativePos, relativeAngle, relativeVelocity, location)
            return true
        }

        val registry = this.subsystem

        val virtualLocation = VirtualLocation.fromLocation(owner)

        val key = getKey(uuid)

        var pocket = registry.getPocketFromKey(key) ?: this.createPocket(key, uuid, virtualLocation) ?: return false

        registry.setCurrentKey(uuid, key) // no-op for private, setPlayerAddress for dialing

        var destLoc = registry.resolveEntrance(uuid)
        if (destLoc == null) {
            DimensionalDoors.LOGGER.info("All entrances are gone, creating a new private pocket!")
            pocket = this.createPocket(key, uuid, virtualLocation) ?: return false

            destLoc = instance.getPocketEntrance(pocket)
        }

        val target = entity(destLoc)

        if (target == null) {
            DimensionalDoors.LOGGER.error(
                "Could not enter private pocket {} for {} because no valid entrance is registered.",
                pocket.id,
                uuid
            )
            this.sendMissingEntranceHint(entity, pocket)
            return false
        }

        return this.processEntity(owner, pocket, target, entity, uuid, relativePos, relativeAngle, relativeVelocity)
    }

    fun onInPocketType(
        entity: Entity?,
        relativePos: Vec3?,
        relativeAngle: Rotations?,
        relativeVelocity: Vec3?,
        location: Location?
    ) {
    }

    fun isCorrectPocketType(owner: Vertex): Boolean {
        return instance.getPocketAt(owner, pocketClass) != null
    }

    val pocketClass: Class<P>

    fun processEntity(
        owner: Vertex,
        pocket: P,
        target: EntityTarget,
        entity: Entity,
        uuid: UUID,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3
    ): Boolean

    fun createPocket(key: O?, playerId: UUID, virtualLocation: VirtualLocation): P? {
        val pocket = createPocket(virtualLocation)

        if (pocket != null) {
            val entrance = instance.getPocketEntrance(pocket)
            if (entrance == null) {
                DimensionalDoors.LOGGER.error(
                    "Could not create dialing pocket {} for {} because no entrance was registered.",
                    pocket.id,
                    playerId
                )
                return null
            }

            val registry = this.subsystem

            registry.setRift(playerId, PlayerTrackerPointer.Variant.Entrance, null as? UUID)
            registry.setRift(playerId, PlayerTrackerPointer.Variant.Exit, null as? UUID)

            registry.setNewPocket(playerId, key, pocket)

            return pocket
        } else {
//            LOGGER.error("Could not create dialing pocket for {} because generation returned {}.", uuid, generatedPocket == null ? "null" : generatedPocket.getClass().getSimpleName());
            return null
        }
    }

    fun sendMissingEntranceHint(entity: Entity?, pocket: P) {
        val owner = entity?.ownerPlayer ?: return

        val origin = pocket.origin
        val virtualLocation = pocket.virtualLocation
        chat(
            owner, Component.literal(
                String.format(
                    "Private pocket entrance missing. Pocket origin: %s @ %d, %d, %d. Virtual coords: x=%d, z=%d, depth=%d.",
                    pocket.world.location(),
                    origin.x,
                    origin.y,
                    origin.z,
                    virtualLocation.x,
                    virtualLocation.z,
                    virtualLocation.depth
                )
            )
        )
    }

    fun getKey(uuid: UUID?): O?

    fun createPocket(virtualLocation: VirtualLocation): P?
}
