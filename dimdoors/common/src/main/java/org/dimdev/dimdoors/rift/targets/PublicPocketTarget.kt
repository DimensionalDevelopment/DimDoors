package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import org.dimdev.dimdoors.pockets.PocketGenerationHelper.PUBLIC
import org.dimdev.dimdoors.pockets.VirtualLocation
import org.dimdev.dimdoors.pockets.VirtualLocation.Companion.fromLocation
import org.dimdev.dimdoors.pockets.generatePocket
import org.dimdev.dimdoors.subsystem.graph.Vertex
import org.dimdev.dimdoors.subsystem.pocket.PocketRegistry.Companion.instance
import org.dimdev.dimdoors.util.Location

object PublicPocketTarget : RestoringTarget<PublicPocketTarget>() {
    override fun makeLinkTarget(owner: Vertex): Location? {
        val riftVirtualLocation = fromLocation(owner)
        val newVirtualLocation: VirtualLocation?
        val depth = riftVirtualLocation.depth + 1
        newVirtualLocation = VirtualLocation(riftVirtualLocation.world, riftVirtualLocation.x, riftVirtualLocation.z, depth)
        val pocket = PUBLIC.generatePocket(newVirtualLocation, owner.providedLocation!!.asTarget())

        return instance.getPocketEntrance(pocket)
    }

    override val type get() = VirtualTargets.PUBLIC_POCKET

    override fun copy(): PublicPocketTarget = PublicPocketTarget

    val codec: MapCodec<PublicPocketTarget> = MapCodec.unit(PublicPocketTarget)
}
