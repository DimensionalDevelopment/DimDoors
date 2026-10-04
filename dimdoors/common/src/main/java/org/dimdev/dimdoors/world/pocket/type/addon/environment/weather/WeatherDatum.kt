package org.dimdev.dimdoors.world.pocket.type.addon.environment.weather

import net.minecraft.world.level.biome.Biome
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimcore.command.TypeArgs
import org.dimdev.dimcore.command.TypeCommands
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys

object WeatherDatum : PlatformRegistry.TypePlatformRegistry<WeatherData>(ModRegistryKeys.WEATHER_DATA, DimensionalDoors.getSided(), true) {
    val EMPTY = create("empty", EmptyWeatherData.codec, EmptyWeatherData.streamCodec)
    val OVERWORLD = create("overworld", OverworldWeatherDataImpl.CODEC, OverworldWeatherDataImpl.STREAM_CODEC)

    init {
        TypeCommands.register(EMPTY, TypeArgs<EmptyWeatherData> { construct { EmptyWeatherData } })

        TypeCommands.register(OVERWORLD, TypeArgs<OverworldWeatherData> {
            val precipitation = word("precipitation", Biome.Precipitation.RAIN.serializedName) { it.precepitation.serializedName }
            val rainLevel = float("rain_level", 0f) { it.rainLevel }

            construct { OverworldWeatherDataImpl(precipitationOf(it[precipitation]), it[rainLevel]) }
        })
    }

    private fun precipitationOf(name: String) = Biome.Precipitation.entries.firstOrNull { it.serializedName == name } ?: Biome.Precipitation.RAIN
}
