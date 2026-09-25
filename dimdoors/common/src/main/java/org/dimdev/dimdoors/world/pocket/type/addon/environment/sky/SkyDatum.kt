package org.dimdev.dimdoors.world.pocket.type.addon.environment.sky

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.EmptySkyData
import org.dimdev.dimdoors.world.pocket.type.addon.sky.OverWorldSkyDataImpl

object SkyDatum : PlatformRegistry.TypePlatformRegistry<SkyData>(ModRegistryKeys.SKY_DATA, DimensionalDoors.getSided()) {
    val EMPTY = create("empty", EmptySkyData.codec, EmptySkyData.streamCodec)
    val END = create("end", EndSkyData.codec, EndSkyData.streamCodec)
    val OVERWORLD = create("overworld", OverWorldSkyDataImpl.CODEC, OverWorldSkyDataImpl.STREAM_CODEC)
}