package org.dimdev.dimdoors.rift.targets

import net.minecraft.resources.ResourceKey
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.pockets.holder
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket

class SelectorTarget(override val template: ResourceKey<VirtualPocket>?) : TemplateTarget<VirtualPocket, SelectorTarget>() {

    override fun copy(): SelectorTarget {
        return SelectorTarget(template)
    }

    override fun processedTemplate(): VirtualPocket? {
        return template?.holder()?.value()
    }

    override val type get() = VirtualTargets.SELECTOR

    companion object {
        val CODEC = codec(ModRegistryKeys.POCKET_GROUPS, ::SelectorTarget)
    }
}
