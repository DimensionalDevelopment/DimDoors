package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.nullableForGetter
import org.dimdev.dimdoors.pockets.PocketGenerator
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.rift.registry.PocketRegistry
import org.dimdev.dimdoors.rift.registry.Vertex
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.pocket.VirtualLocation
import kotlin.jvm.optionals.getOrNull

class TemplateTarget(private val template: Holder<VirtualPocket>?) : RestoringTarget<TemplateTarget>() {
    override fun makeLinkTarget(owner: Vertex): Location? {
        val template = template ?: return null
        val riftVirtualLocation = VirtualLocation.fromLocation(owner)
        val newVirtualLocation: VirtualLocation?
        val depth = riftVirtualLocation.depth + 1
        newVirtualLocation = VirtualLocation(
            riftVirtualLocation.world,
            riftVirtualLocation.x,
            riftVirtualLocation.z,
            depth
        )
        val pocket = PocketGenerator.generateFromVirtualPocket(
            DimensionalDoors.getWorld(ModDimensions.DUNGEON),
            template,
            newVirtualLocation,
            owner.providedLocation!!.asTarget(),
            null
        )

        return PocketRegistry.instance.getPocketEntrance(pocket)
    }

    override fun copy(): TemplateTarget {
        return TemplateTarget(template)
    }

    override val type get() = VirtualTargets.TEMPLATE

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group(
                VirtualPocket.HOLDER_CODEC.lenientOptionalFieldOf("template").nullableForGetter(TemplateTarget::template)
            ).apply(instance) { TemplateTarget(it.getOrNull()) }
        }
    }
}
