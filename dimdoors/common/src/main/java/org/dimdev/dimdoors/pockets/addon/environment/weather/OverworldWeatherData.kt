package org.dimdev.dimdoors.pockets.addon.environment.weather

import net.minecraft.world.level.biome.Biome

interface OverworldWeatherData : WeatherData {
    override val type get() = WeatherDatum.OVERWORLD
    val precepitation: Biome.Precipitation
    val rainLevel: Float
}
