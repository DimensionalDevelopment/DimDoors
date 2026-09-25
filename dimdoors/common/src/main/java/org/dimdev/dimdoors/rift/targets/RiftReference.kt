package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.api.rift.target.TargetResolver
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.RGBA
import org.dimdev.dimdoors.rift.registry.RiftRegistry

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
class RiftReference(private val target: Location?) : VirtualTarget<RiftReference>(), LocationProvider {
    override fun getLocation(): Location? {
        return target
    }

    override fun receiveOther(): Target? {
        return TargetResolver.target(this.target)
    }

    override fun register() {
        RiftRegistry.getInstance().addLink(this.location, this.target)
    }

     override fun unregister() {
        if (this.location != null) RiftRegistry.getInstance().removeLink(this.location, this.target)
    }

    override fun shouldInvalidate(riftDeleted: Location): Boolean {
        // A rift we may have asked the registry to notify us about was deleted
        return riftDeleted == this.target
    }

    override val color: RGBA
        get() {
            if (target != null && RiftRegistry.getInstance().isRiftAt(target)) {
                val otherRiftTargets =
                    RiftRegistry.getInstance().getTargets(target)
                if (otherRiftTargets.size == 1 && otherRiftTargets.contains(this.location)) {
                    return RGBA(0f, 1f, 0f, 1f)
                }
            }
            return RGBA(1f, 0f, 0f, 1f)
        }

    override val type get() = VirtualTargets.RIFT_REFERENCE

    override fun copy(): RiftReference {
        return RiftReference(location)
    }

    companion object {
        var CODEC: MapCodec<RiftReference> = Location.CODEC.fieldOf("target").xmap(::RiftReference, RiftReference::getLocation)
    }
}
