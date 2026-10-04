package org.dimdev.dimdoors.world.pocket.type.addon.environment.sky

import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimcore.command.TypeArgs
import org.dimdev.dimcore.command.TypeCommands
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.EmptySkyData
import org.dimdev.dimdoors.world.pocket.type.addon.sky.OverWorldSkyDataImpl

object SkyDatum : PlatformRegistry.TypePlatformRegistry<SkyData>(ModRegistryKeys.SKY_DATA, DimensionalDoors.getSided(), true) {
    val EMPTY = create("empty", EmptySkyData.codec, EmptySkyData.streamCodec)
    val END = create("end", EndSkyData.codec, EndSkyData.streamCodec)
    val OVERWORLD = create("overworld", OverWorldSkyDataImpl.CODEC, OverWorldSkyDataImpl.STREAM_CODEC)

    init {
        TypeCommands.register(EMPTY, TypeArgs<EmptySkyData> { construct { EmptySkyData } })
        TypeCommands.register(END, TypeArgs<EndSkyData> { construct { EndSkyData } })

        TypeCommands.register(OVERWORLD, TypeArgs<OverWorldSkyData> {
            val dayTime = long("day_time", 12000L) { it.dayTime }
            val moonPhase = int("moon_phase", 0) { it.moonPhase }
            val skyColor = vec3("sky_color", Vec3(0.486, 0.654, 1.0)) { it.skyColor }
            val rainLevel = float("rain_level", 0f) { it.rainLevel }
            val thunderLevel = float("thunder_level", 0f) { it.thunderLevel }

            construct { OverWorldSkyDataImpl(it[dayTime], it[moonPhase], it[skyColor], it[rainLevel], it[thunderLevel]) }
        })
    }
}
