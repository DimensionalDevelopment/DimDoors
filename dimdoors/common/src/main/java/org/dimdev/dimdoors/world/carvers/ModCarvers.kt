package org.dimdev.dimdoors.world.carvers

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.util.key

object ModCarvers : PlatformRegistry.ConfiguredCarverPlatformRegistry(DimensionalDoors.getSided()) {
    val LIMBO_CARVER = create("limbo") { LimboCarver(CaveCarverConfiguration.CODEC) }

    val LIMBO: ResourceKey<ConfiguredWorldCarver<*>> = Registries.CONFIGURED_CARVER.key("limbo")
}
