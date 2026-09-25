package org.dimdev.dimdoors.world.pocket.type.addon.environment.weather

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys

object WeatherDatum : PlatformRegistry.TypePlatformRegistry<WeatherData>(ModRegistryKeys.WEATHER_DATA, DimensionalDoors.getSided()) {
    val EMPTY = create("empty", EmptyWeatherData.codec, EmptyWeatherData.streamCodec)
    val OVERWORLD = create("overworld", OverworldWeatherDataImpl.CODEC, OverworldWeatherDataImpl.STREAM_CODEC)
}