package org.dimdev.dimdoors.rift.targets

import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.blockentity.RiftBlockEntity
import org.dimdev.dimdoors.subsystem.graph.Vertex
import org.dimdev.dimdoors.util.Location

abstract class RestoringTarget<T : VirtualTarget<T>> : VirtualTarget<T>() {
    override fun receiveOther(owner: Vertex): Target? {
        val reference = this.makeLinkTarget(owner)?.asTarget() ?: return null
        val rift = owner.providedLocation?.blockEntity?.castOrNull<RiftBlockEntity<*>>() ?: return null
        rift.setDestination(reference)
        return reference

    }

    abstract fun makeLinkTarget(owner: Vertex): Location?
}
