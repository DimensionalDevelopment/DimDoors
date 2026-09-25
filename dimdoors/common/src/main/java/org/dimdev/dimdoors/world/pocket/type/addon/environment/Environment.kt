package org.dimdev.dimdoors.world.pocket.type.addon.environment

import org.dimdev.dimcore.api.TypeHasHolder
import org.dimdev.dimdoors.ModRegistries
import org.dimdev.dimdoors.util.CodecUtils.holderCodec
import org.dimdev.dimdoors.util.holderStreamCodec
import org.dimdev.dimdoors.util.streamCodec
import org.dimdev.dimdoors.world.pocket.type.addon.environment.EmptyEnvironment.codec
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.CloudData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.SkyData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.weather.WeatherData


interface Environment : TypeHasHolder<Environment> {
    val sky: SkyData
    val cloud: CloudData
    val weather: WeatherData

    companion object {
        val CODEC = Environments.codec
        val STREAM_CODEC = Environments.streamCodec
    }

}
