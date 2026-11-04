package org.dimdev.dimdoors.pockets.modifier

import com.mojang.serialization.MapCodec
import net.minecraft.resources.ResourceKey
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.rift.targets.SelectorTarget

class SelectorModifier(ids: MutableList<Int>, override val template: ResourceKey<VirtualPocket>) : TemplateModifier<VirtualPocket, SelectorTarget>(ids) {

    override val type: MapCodec<out Modifier> get() = Modifiers.SELECTOR
    override fun createTarget(key: ResourceKey<VirtualPocket>): SelectorTarget = SelectorTarget(key)

    companion object {
        val CODEC = codec(ModRegistryKeys.POCKET_GROUPS, ::SelectorModifier)
    }
}
