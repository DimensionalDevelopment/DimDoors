package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.pockets.PocketGenerator
import org.dimdev.dimdoors.rift.registry.PocketRegistry.Companion.instance
import org.dimdev.dimdoors.world.pocket.VirtualLocation
import org.dimdev.dimdoors.world.pocket.VirtualLocation.Companion.fromLocation
import java.util.function.Supplier

object PublicPocketTarget : RestoringTarget<PublicPocketTarget>() {
    override fun makeLinkTarget(): Location? {
        val riftVirtualLocation = fromLocation(this.location)
        val newVirtualLocation: VirtualLocation?
        val depth = riftVirtualLocation.depth + 1
        newVirtualLocation =
            VirtualLocation(riftVirtualLocation.world, riftVirtualLocation.x, riftVirtualLocation.z, depth)
        val pocket = PocketGenerator.generatePublicPocketV2(newVirtualLocation, RiftReference(this.location), null)

        return instance.getPocketEntrance(pocket)
    }

    override val type get() = VirtualTargets.PUBLIC_POCKET

    override fun copy(): PublicPocketTarget = PublicPocketTarget

    val codec = MapCodec.unit(PublicPocketTarget)
}
