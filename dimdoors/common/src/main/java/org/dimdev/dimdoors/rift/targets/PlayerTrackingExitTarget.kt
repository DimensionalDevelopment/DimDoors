package org.dimdev.dimdoors.rift.targets

import net.minecraft.core.Rotations
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.util.EntityUtils
import org.dimdev.dimcore.api.ext.ownerPlayerUuid
import org.dimdev.dimdoors.api.rift.target.EntityTarget
import org.dimdev.dimdoors.api.rift.target.TargetResolver.entity
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.registry.PlayerTrackingSubSystem
import org.dimdev.dimdoors.rift.registry.PocketRegistry
import org.dimdev.dimdoors.world.pocket.type.Pocket

abstract class PlayerTrackingExitTarget<T : PlayerTrackingExitTarget<T, S>, S : PlayerTrackingSubSystem<*, *, *>> : VirtualTarget<T>(), EntityTarget {
    override fun receiveEntity(
        entity: Entity,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3,
        location: Location?
    ): Boolean {
        // TODO: make this recursive
        val uuid = entity.ownerPlayerUuid ?: return false // Non-player/owned entity tried to escape/leave private pocket

        val registry = this.subsystem

        val destLoc = registry.getExitLocation(uuid)
        val pocket: Pocket<*, *>? = registry.getPocketFromPlayer(uuid)
        if (registry.isCorrectDimensionForPocket(this.location.world) && pocket != null) {
            val currentPocket =
                PocketRegistry.instance.getPocketDirectory(pocket.world).getPocketAt(this.location.blockPos)
            if (pocket == currentPocket) {
                registry.setEntrance(
                    uuid,
                    this.location
                ) // Remember which exit was used for next time the pocket is entered
            }
        }

        val target = entity(destLoc)

        if (target == null) {
            if (destLoc == null) {
                EntityUtils.chat(
                    entity,
                    Component.translatable("rifts.destinations.private_pocket_exit.did_not_use_rift")
                )
            } else {
                EntityUtils.chat(
                    entity,
                    Component.translatable("rifts.destinations.private_pocket_exit.rift_has_closed")
                )
            }

            LimboTarget.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, location)

            return false
        }

        return target.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, destLoc)
    }

    override fun register() {
        super.register()
        val registry = PocketRegistry.instance.getPocketDirectory(this.location.worldId)
        val pocket = registry.getPocketAt(this.location.blockPos)
        if (pocket != null) {
            PocketRegistry.instance.addPocketEntrance(pocket, this.location)
        }
    }

    abstract val subsystem: S
}
