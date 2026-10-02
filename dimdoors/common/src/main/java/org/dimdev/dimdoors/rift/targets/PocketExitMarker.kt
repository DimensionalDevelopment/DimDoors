package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import net.minecraft.core.Rotations
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.util.EntityUtils.chat
import org.dimdev.dimdoors.api.rift.target.EntityTarget
import org.dimdev.dimdoors.api.util.Location

object PocketExitMarker : VirtualTarget<PocketExitMarker>(), EntityTarget {
    override fun receiveEntity(
        entity: Entity,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3,
        location: Location?
    ): Boolean {
        chat(
            entity,
            Component.literal("The exit of this dungeon has not been linked. If this is a normally generated pocket, please report this bug.")
        )

        return false
    }

    override val type get() = VirtualTargets.POCKET_EXIT

    override fun copy(): PocketExitMarker = PocketExitMarker

    val codec = MapCodec.unit(PocketExitMarker)
}