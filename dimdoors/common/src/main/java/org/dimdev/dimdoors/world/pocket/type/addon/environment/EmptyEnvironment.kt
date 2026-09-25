package org.dimdev.dimdoors.world.pocket.type.addon.environment

import org.dimdev.dimdoors.SingletonInstance
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.CloudData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.EmptyCloudData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.EmptySkyData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.SkyData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.weather.EmptyWeatherData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.weather.WeatherData

object EmptyEnvironment : Environment, SingletonInstance<EmptyEnvironment>() {
    override val sky: SkyData get() = EmptySkyData
    override val cloud: CloudData get() = EmptyCloudData
    override val weather: WeatherData get() = EmptyWeatherData

    override val type get() = Environments.EMPTY
}
