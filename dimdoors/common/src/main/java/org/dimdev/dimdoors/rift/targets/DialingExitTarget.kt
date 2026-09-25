package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import org.dimdev.dimdoors.rift.registry.DialingRegistry
import org.dimdev.dimdoors.rift.registry.DialingRegistry.Companion.instance

object DialingExitTarget : PlayerTrackingExitTarget<DialingExitTarget, DialingRegistry>() {
    override val type get() = VirtualTargets.DIALING_EXIT

    override fun copy(): DialingExitTarget = DialingExitTarget

    override val subsystem get() = DialingRegistry.instance

    val codec = MapCodec.unit(DialingExitTarget)
}
