package org.dimdev.dimdoors.world.pocket.type.addon.environment.weather

import net.minecraft.core.Holder
import org.dimdev.dimcore.api.Type
import org.dimdev.dimdoors.SingletonInstance

object EmptyWeatherData : WeatherData, SingletonInstance<EmptyWeatherData>() {
    override val type: Holder<out Type<WeatherData>> get() = WeatherDatum.EMPTY
}
