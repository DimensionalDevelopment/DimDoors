package org.dimdev.dimdoors.pockets.addon.environment

import org.dimdev.dimdoors.pockets.addon.environment.cloud.CloudData
import org.dimdev.dimdoors.pockets.addon.environment.cloud.EmptyCloudData
import org.dimdev.dimdoors.pockets.addon.environment.sky.EndSkyData
import org.dimdev.dimdoors.pockets.addon.environment.sky.SkyData
import org.dimdev.dimdoors.pockets.addon.environment.weather.EmptyWeatherData
import org.dimdev.dimdoors.pockets.addon.environment.weather.WeatherData
import org.dimdev.dimdoors.util.codec.SingletonInstance

object EndEnvironment : Environment, SingletonInstance<EndEnvironment>() {
    override val sky: SkyData get() = EndSkyData
    override val cloud: CloudData get() = EmptyCloudData
    override val weather: WeatherData get() = EmptyWeatherData

    override val type get() = Environments.END
}

