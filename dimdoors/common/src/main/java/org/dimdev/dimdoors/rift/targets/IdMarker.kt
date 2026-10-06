package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.core.Rotations
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.util.EntityUtils.chat
import org.dimdev.dimdoors.api.rift.target.EntityTarget
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.registry.Vertex

class IdMarker(val id: Int) : VirtualTarget<IdMarker>(), EntityTarget {
    override val type get() = VirtualTargets.ID_MARKER

    override fun copy(): IdMarker = IdMarker(id)

    override fun receiveEntity(
        owner: Vertex,
        entity: Entity,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3,
        location: Location?
    ): Boolean {
        chat(entity, Component.literal("This rift is configured for pocket dungeons. Its id is " + this.id))
        return false
    }

    companion object {
        val CODEC: MapCodec<IdMarker> = Codec.INT.xmap(::IdMarker, IdMarker::id)
                .fieldOf("id")
    }
}