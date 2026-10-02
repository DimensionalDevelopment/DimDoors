package org.dimdev.dimdoors.world.feature

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import org.dimdev.dimdoors.api.util.key

@Suppress("unused")
class ModFeatures {
    object Configured {
        val SOLID_STATIC_ORE = Registries.CONFIGURED_FEATURE.key("solid_static_ore")
        val DECAYED_BLOCK_ORE = Registries.CONFIGURED_FEATURE.key("decayed_block_ore")
        val ETERNAL_FLUID_SPRING = Registries.CONFIGURED_FEATURE.key("eternal_fluid_spring")
        val DRIFTWOOD_TREE = Registries.CONFIGURED_FEATURE.key("driftwood_tree")
    }

    object Placed {
        val SOLID_STATIC_ORE: ResourceKey<PlacedFeature?> = Registries.PLACED_FEATURE.key("solid_static_ore")
        val DECAYED_BLOCK_ORE: ResourceKey<PlacedFeature?> = Registries.PLACED_FEATURE.key("decayed_block_ore")
        val ETERNAL_FLUID_SPRING: ResourceKey<PlacedFeature?> = Registries.PLACED_FEATURE.key("eternal_fluid_spring")
    }
}