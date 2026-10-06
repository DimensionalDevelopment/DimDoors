package org.dimdev.dimdoors.rift.targets

import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.rift.registry.Vertex

abstract class RestoringTarget<T : VirtualTarget<T>> : VirtualTarget<T>() {
    override fun receiveOther(owner: Vertex): Target? {
        val reference = this.makeLinkTarget(owner)?.asTarget() ?: return null
        val rift = owner.providedLocation?.blockEntity?.castOrNull<Rift>() ?: return null
        rift.setDestination(reference)
        return reference

    }

    abstract fun makeLinkTarget(owner: Vertex): Location?
}
