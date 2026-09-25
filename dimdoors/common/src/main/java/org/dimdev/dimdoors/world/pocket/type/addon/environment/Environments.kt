package org.dimdev.dimdoors.world.pocket.type.addon.environment

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.OverworldEnvironment

object Environments : PlatformRegistry.TypePlatformRegistry<Environment>(ModRegistryKeys.ENVIRONMENT, DimensionalDoors.getSided()) {
    val EMPTY = create("empty", EmptyEnvironment.codec, EmptyEnvironment.streamCodec)
    val COMPLEX = create("complex", ComplexEnvironment.CODEC, ComplexEnvironment.STREAM_CODEC)
    val END = create("end", EndEnvironment.codec, EndEnvironment.streamCodec)
    val OVERWORLD = create("overworld", OverworldEnvironment.CODEC, OverworldEnvironment.STREAM_CODEC)
}