package org.dimdev.dimdoors.world.pocket.type.addon.environment.weather

import org.dimdev.dimcore.api.TypeHasHolder

interface WeatherData : TypeHasHolder<WeatherData> {
    companion object {
        val CODEC = WeatherDatum.codec
        val STREAM_CODEC = WeatherDatum.streamCodec
    }
}
