package org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud

import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimcore.command.TypeArgs
import org.dimdev.dimcore.command.TypeCommands
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys

object CloudDatum : PlatformRegistry.TypePlatformRegistry<CloudData>(ModRegistryKeys.CLOUD_DATA, DimensionalDoors.getSided(), true) {
    val EMPTY = create("empty", EmptyCloudData.codec, EmptyCloudData.streamCodec)
    val OVERWORLD = create("overworld", OverworldCloudDataImpl.CODEC, OverworldCloudDataImpl.STREAM_CODEC)

    init {
        TypeCommands.register(EMPTY, TypeArgs<EmptyCloudData> { construct { EmptyCloudData } })

        TypeCommands.register(OVERWORLD, TypeArgs<OverworldCloudData> {
            val height = float("height", 128f) { it.cloudHeight }
            val color = vec3("color", Vec3(1.0, 1.0, 1.0)) { it.cloudColor }

            construct { OverworldCloudDataImpl(it[height], it[color]) }
        })
    }
}
