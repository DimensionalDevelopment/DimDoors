package org.dimdev.dimdoors.world.pocket.type.addon.environment.weather

import net.minecraft.core.Holder
import net.minecraft.world.level.biome.Biome
import org.dimdev.dimcore.api.Type

interface OverworldWeatherData : WeatherData {
    override val type get() = WeatherDatum.OVERWORLD
    val precepitation: Biome.Precipitation
    val rainLevel: Float
}
