package org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys

object CloudDatum : PlatformRegistry.TypePlatformRegistry<CloudData>(ModRegistryKeys.CLOUD_DATA, DimensionalDoors.getSided()) {
    val EMPTY = create("empty", EmptyCloudData.codec, EmptyCloudData.streamCodec)
    val OVERWORLD = create("overworld", OverworldCloudDataImpl.CODEC, OverworldCloudDataImpl.STREAM_CODEC)
}