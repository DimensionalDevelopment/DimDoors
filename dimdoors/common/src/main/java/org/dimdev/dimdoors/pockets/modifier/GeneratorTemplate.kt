package org.dimdev.dimdoors.pockets.modifier

import com.mojang.serialization.MapCodec
import net.minecraft.resources.ResourceKey
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.pockets.generator.PocketGenerator
import org.dimdev.dimdoors.rift.targets.GeneratorTarget

class GeneratorModifier(ids: MutableList<Int>, override val template: ResourceKey<PocketGenerator<*>>) : TemplateModifier<PocketGenerator<*>, GeneratorTarget>(ids) {

    override fun createTarget(key: ResourceKey<PocketGenerator<*>>): GeneratorTarget = GeneratorTarget(key)
    override val type: MapCodec<out Modifier> get() = Modifiers.GENERATOR

    companion object {
        val CODEC = codec(ModRegistryKeys.POCKET_GENERATOR, ::GeneratorModifier)
    }
}
