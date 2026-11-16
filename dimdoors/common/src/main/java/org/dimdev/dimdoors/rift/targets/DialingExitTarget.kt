package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import org.dimdev.dimdoors.subsystem.player.DialingRegistry
import org.dimdev.dimdoors.subsystem.player.DialingRegistry.Companion.instance

object DialingExitTarget : PlayerTrackingExitTarget<DialingExitTarget, DialingRegistry>() {
    override val type get() = VirtualTargets.DIALING_EXIT

    override fun copy(): DialingExitTarget = DialingExitTarget

    override val subsystem get() = instance

    val codec: MapCodec<DialingExitTarget> = MapCodec.unit(DialingExitTarget)
}
