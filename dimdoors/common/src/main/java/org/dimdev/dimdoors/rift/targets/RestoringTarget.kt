package org.dimdev.dimdoors.rift.targets

import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.block.entity.Rift

abstract class RestoringTarget<T : VirtualTarget<T>> : VirtualTarget<T>() {
    override fun receiveOther(): Target? {
        val reference = this.makeLinkTarget()?.asTarget() ?: return null
        val rift = this.location.blockEntity?.castOrNull<Rift>() ?: return null
        rift.setDestination(reference)
        return reference

    }

    abstract fun makeLinkTarget(): Location?
}
