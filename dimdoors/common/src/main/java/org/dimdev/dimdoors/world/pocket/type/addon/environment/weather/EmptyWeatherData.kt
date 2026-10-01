package org.dimdev.dimdoors.world.pocket.type.addon.environment.weather

import org.dimdev.dimdoors.SingletonInstance

object EmptyWeatherData : WeatherData, SingletonInstance<EmptyWeatherData>() {
    override val type get() = WeatherDatum.EMPTY
}
