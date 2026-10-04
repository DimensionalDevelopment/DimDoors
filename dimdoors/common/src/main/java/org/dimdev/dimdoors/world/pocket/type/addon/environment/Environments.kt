package org.dimdev.dimdoors.world.pocket.type.addon.environment

import net.minecraft.world.level.biome.Biome
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimcore.command.TypeArgs
import org.dimdev.dimcore.command.TypeCommands
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.CloudDatum
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.EmptyCloudData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.EmptySkyData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.OverworldEnvironment
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.SkyDatum
import org.dimdev.dimdoors.world.pocket.type.addon.environment.weather.EmptyWeatherData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.weather.WeatherDatum

object Environments : PlatformRegistry.TypePlatformRegistry<Environment>(ModRegistryKeys.ENVIRONMENT, DimensionalDoors.getSided(), true) {
    val EMPTY = create("empty", EmptyEnvironment.codec, EmptyEnvironment.streamCodec)
    val COMPLEX = create("complex", ComplexEnvironment.CODEC, ComplexEnvironment.STREAM_CODEC)
    val END = create("end", EndEnvironment.codec, EndEnvironment.streamCodec)
    val OVERWORLD = create("overworld", OverworldEnvironment.CODEC, OverworldEnvironment.STREAM_CODEC)

    init {
        TypeCommands.register(EMPTY, TypeArgs<EmptyEnvironment> { construct { EmptyEnvironment } })
        TypeCommands.register(END, TypeArgs<EndEnvironment> { construct { EndEnvironment } })
        TypeCommands.register(COMPLEX, TypeArgs<ComplexEnvironment> {
            val sky = nested("sky", SkyDatum.registry, EmptySkyData) { it.sky }
            val weather = nested("weather", WeatherDatum.registry, EmptyWeatherData) { it.weather }
            val cloud = nested("cloud", CloudDatum.registry, EmptyCloudData) { it.cloud }

            construct { ComplexEnvironment(it[sky], it[weather], it[cloud]) }
        })

        TypeCommands.register(OVERWORLD, TypeArgs<OverworldEnvironment> {
            val dayTime = long("day_time", 12000L) { it.dayTime }
            val moonPhase = int("moon_phase", 0) { it.moonPhase }
            val skyColor = vec3("sky_color", Vec3(0.486, 0.654, 1.0)) { it.skyColor }
            val rainLevel = float("rain_level", 0f) { it.rainLevel }
            val precipitation = word("precipitation", Biome.Precipitation.NONE.serializedName) { it.precipitation.serializedName }
            val thunderLevel = float("thunder_level", 0f) { it.thunderLevel }
            val cloudHeight = float("cloud_height", 128f) { it.cloudHeight }
            val cloudColor = vec3("cloud_color", Vec3(1.0, 1.0, 1.0)) { it.cloudColor }

            construct {
                OverworldEnvironment(
                    it[dayTime], it[moonPhase], it[skyColor], it[rainLevel],
                    precipitationOf(it[precipitation]), it[thunderLevel], it[cloudHeight], it[cloudColor]
                )
            }
        })
    }

    private fun precipitationOf(name: String) = Biome.Precipitation.entries.firstOrNull { it.serializedName == name } ?: Biome.Precipitation.NONE
}
