package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import org.dimdev.dimdoors.api.util.RGBA
import org.dimdev.dimdoors.rift.registry.Vertex
import org.dimdev.dimdoors.rift.registry.HubRegistry

object HubPocketExitTarget : PlayerTrackingExitTarget<HubPocketExitTarget, HubRegistry>() {
    val color: RGBA = RGBA(0f, 0.5f, 1f, 1f)

    override val type get() = VirtualTargets.HUB_EXIT

    override fun copy(): HubPocketExitTarget {
        return this
    }

    override fun getColor(owner: Vertex): RGBA = color

    override val subsystem: HubRegistry get() = HubRegistry.instance

    val codec: MapCodec<HubPocketExitTarget> = MapCodec.unit(HubPocketExitTarget)
}
