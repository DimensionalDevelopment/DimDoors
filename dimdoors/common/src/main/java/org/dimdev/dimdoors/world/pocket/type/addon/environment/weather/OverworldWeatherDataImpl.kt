package org.dimdev.dimdoors.world.pocket.type.addon.environment.weather

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.level.biome.Biome

data class OverworldWeatherDataImpl(override val precepitation: Biome.Precipitation, override val rainLevel: Float) : OverworldWeatherData {

    companion object {
        val CODEC: MapCodec<OverworldWeatherData> = RecordCodecBuilder.mapCodec { instance -> instance.group(
                Biome.Precipitation.CODEC.optionalFieldOf("precipitation", Biome.Precipitation.RAIN).forGetter(OverworldWeatherData::precepitation),
                Codec.floatRange(0f, 1f).optionalFieldOf("rainLevel", 0f).forGetter(OverworldWeatherData::rainLevel)
            ).apply(instance, ::OverworldWeatherDataImpl)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, OverworldWeatherData> = StreamCodec.of(
            { buf, data ->
                buf.writeEnum(data.precepitation)
                buf.writeFloat(data.rainLevel)
            },
            { buf -> OverworldWeatherDataImpl(buf.readEnum(Biome.Precipitation::class.java), buf.readFloat()) }
        )
    }
}
