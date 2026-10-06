package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import org.dimdev.dimdoors.api.util.RGBA
import org.dimdev.dimdoors.rift.registry.Vertex
import org.dimdev.dimdoors.world.pocket.PrivateRegistry

object PrivatePocketExitTarget : PlayerTrackingExitTarget<PrivatePocketExitTarget, PrivateRegistry>() {
    val color: RGBA = RGBA(0f, 1f, 0f, 1f)

    override val type get() = VirtualTargets.PRIVATE_POCKET_EXIT

    override fun copy(): PrivatePocketExitTarget {
        return this
    }

    override fun getColor(owner: Vertex): RGBA = color

    override val subsystem: PrivateRegistry get() = PrivateRegistry.instance

    val codec: MapCodec<PrivatePocketExitTarget> = MapCodec.unit(PrivatePocketExitTarget)
}
