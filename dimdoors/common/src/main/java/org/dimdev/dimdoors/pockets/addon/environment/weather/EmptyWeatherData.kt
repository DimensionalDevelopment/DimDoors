package org.dimdev.dimdoors.pockets.addon.environment.weather

import org.dimdev.dimdoors.util.codec.SingletonInstance

object EmptyWeatherData : WeatherData, SingletonInstance<EmptyWeatherData>() {
    override val type get() = WeatherDatum.EMPTY
}
