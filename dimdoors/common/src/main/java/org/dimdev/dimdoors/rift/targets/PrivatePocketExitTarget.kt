package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import org.dimdev.dimdoors.api.util.RGBA
import org.dimdev.dimdoors.world.pocket.PrivateRegistry

object PrivatePocketExitTarget : PlayerTrackingExitTarget<PrivatePocketExitTarget, PrivateRegistry>() {
    val type get() = VirtualTargets.PRIVATE_POCKET_EXIT

    override fun copy(): PrivatePocketExitTarget {
        return this
    }

    override val color: RGBA = RGBA(0f, 1f, 0f, 1f)

    override val subsystem: PrivateRegistry get() = PrivateRegistry.instance

    val codec = MapCodec.unit(PrivatePocketExitTarget)
}
