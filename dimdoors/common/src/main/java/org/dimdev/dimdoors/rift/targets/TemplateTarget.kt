package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.pockets.PocketGenerator
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.rift.registry.PocketRegistry
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.pocket.VirtualLocation

class TemplateTarget(private val template: Holder<VirtualPocket>) : RestoringTarget<TemplateTarget>() {
    override fun makeLinkTarget(): Location? {
        val riftVirtualLocation = VirtualLocation.fromLocation(this.location)
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
            this.location.asTarget(),
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
                VirtualPocket.HOLDER_CODEC.fieldOf("template").forGetter(TemplateTarget::template)
            ).apply(instance, ::TemplateTarget)
        }
    }
}
