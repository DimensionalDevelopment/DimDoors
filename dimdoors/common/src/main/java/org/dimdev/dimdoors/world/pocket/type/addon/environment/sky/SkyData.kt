package org.dimdev.dimdoors.world.pocket.type.addon.environment.sky

import org.dimdev.dimcore.api.TypeHasHolder
import org.dimdev.dimdoors.world.pocket.type.addon.environment.weather.WeatherDatum

interface SkyData : TypeHasHolder<SkyData> {
    companion object {
        val CODEC = WeatherDatum.codec
        val STREAM_CODEC = WeatherDatum.streamCodec
    }
}
