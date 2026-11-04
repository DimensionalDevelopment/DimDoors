package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import net.minecraft.resources.ResourceKey
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.pockets.generator.PocketGenerator
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.pockets.virtual.reference.IdReference

class GeneratorTarget(override val template: ResourceKey<PocketGenerator<*>>?) : TemplateTarget<PocketGenerator<*>, GeneratorTarget>() {

    override fun copy(): GeneratorTarget {
        return GeneratorTarget(template)
    }

    override fun processedTemplate(): VirtualPocket? {
        return template?.let { IdReference(id = it) }
    }

    override val type: MapCodec<GeneratorTarget> get() = VirtualTargets.GENERATOR

    companion object {
        val CODEC = TemplateTarget.codec(ModRegistryKeys.POCKET_GENERATOR, ::GeneratorTarget)
    }
}
