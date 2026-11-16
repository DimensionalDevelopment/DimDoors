package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import org.dimdev.dimcore.api.errata.schema.Schemas
import org.dimdev.dimdoors.pockets.PocketGenerationHelper
import org.dimdev.dimdoors.pockets.VirtualLocation
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.subsystem.graph.RiftGraph
import org.dimdev.dimdoors.subsystem.graph.Vertex
import org.dimdev.dimdoors.subsystem.pocket.PocketRegistry
import org.dimdev.dimdoors.util.Location
import org.dimdev.dimdoors.util.ext.nullableForGetter
import kotlin.jvm.optionals.getOrNull

abstract class TemplateTarget<V, T: TemplateTarget<V, T>> : RestoringTarget<T>() {
    abstract val template: ResourceKey<V>?

    override fun makeLinkTarget(owner: Vertex): Location? {
        val template = processedTemplate() ?: return null
        val virtualLocation = VirtualLocation.fromLocation(owner).run { copy(depth = depth + 1) }

        val pocket = PocketGenerationHelper.generateFromVirtualPocket(
            template,
            virtualLocation,
            owner.providedLocation!!.asTarget(),
            null
        )

        val parent = PocketRegistry.instance.getPocketAt(owner)
        if (parent != null && pocket != null) RiftGraph.getInstance().chained(parent, pocket)

        return PocketRegistry.instance.getPocketEntrance(pocket)
    }

    abstract fun processedTemplate(): VirtualPocket?

    companion object {
        fun <T : Any, V : TemplateTarget<T, V>> codec(key: ResourceKey<Registry<T>>, function: (ResourceKey<T>?) -> V): MapCodec<V> =
            RecordCodecBuilder.mapCodec { instance -> instance.group(
                Schemas.registryKey(key).lenientOptionalFieldOf("template").nullableForGetter { it.template }
            ).apply(instance) { function(it.getOrNull()) }
            }
    }
}
