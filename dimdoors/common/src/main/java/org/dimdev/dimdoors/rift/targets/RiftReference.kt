package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.api.rift.target.TargetResolver
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.RGBA
import org.dimdev.dimdoors.rift.registry.RiftGraph
import org.dimdev.dimdoors.rift.registry.RiftPlaceholder
import org.dimdev.dimdoors.rift.registry.Vertex
import org.dimdev.dimdoors.util.UUIDExtensions.rift

/**
 * Allows rifts and targets to reference another rift without having to
 * actually load the rift's chunk and get its tile entity (which could lead
 * to recursively loading many chunks to load a single rift's chunk).
 *
 *
 * Information about the referenced rift's location is stored in the RiftRegistry
 * such that when the target rift is gone, the destination is notified and invalidated
 * (see shouldInvalidate)
 */
class RiftReference(private val target: Vertex) : VirtualTarget<RiftReference>(), LocationProvider {
    override val providedLocation: Location?
        get() = target.providedLocation

    override fun receiveOther(owner: Vertex): Target? {
        return TargetResolver.target(this.target.providedLocation)
    }

    override fun register(owner: Vertex) {
        RiftGraph.getInstance().addEdge(owner.id, this.target.id)
    }

    override fun unregister(owner: Vertex) {
        RiftGraph.getInstance().removeEdge(owner.id, this.target.id)
    }

    override fun shouldInvalidate(riftDeleted: Location): Boolean = target.providedLocation == null

    override fun getColor(owner: Vertex): RGBA {
        val graph = RiftGraph.getInstance()
        val type = graph.vertex(target.id)
        if (type != null && type !== RiftPlaceholder && graph.targets(target.id) == setOf(owner.id)) {
            return RGBA(0f, 1f, 0f, 1f)
        }
        return RGBA(1f, 0f, 0f, 1f)
    }

    override val type get() = VirtualTargets.RIFT_REFERENCE

    override fun copy(): RiftReference {
        return RiftReference(target)
    }

    override fun equals(other: Any?) = other is RiftReference && other.target == target

    override fun hashCode() = target.hashCode()

    companion object {
        fun from(level: ServerLevel, pos: BlockPos): Vertex = Location.ofWorld(level, pos).riftOrPlaceholder().rift()

        var CODEC: MapCodec<RiftReference> = Vertex.CODEC.fieldOf("target").xmap(::RiftReference) { it.target }
    }
}
