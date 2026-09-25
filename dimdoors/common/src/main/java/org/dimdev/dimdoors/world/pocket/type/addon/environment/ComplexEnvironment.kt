package org.dimdev.dimdoors.world.pocket.type.addon.environment

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.codec.StreamCodec.composite
import org.dimdev.dimcore.api.Type
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.CloudData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.EmptyCloudData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.EmptySkyData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.SkyData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.weather.EmptyWeatherData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.weather.WeatherData

data class ComplexEnvironment(
    override val sky: SkyData = EmptySkyData,
    override val weather: WeatherData = EmptyWeatherData,
    override val cloud: CloudData = EmptyCloudData
) : Environment {
    override val type: Holder<out Type<Environment>> get() = Environments.COMPLEX

    companion object {
        val CODEC: MapCodec<ComplexEnvironment> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                SkyData.CODEC.optionalFieldOf("sky", EmptySkyData).forGetter(ComplexEnvironment::sky),
                WeatherData.CODEC.optionalFieldOf("weather", EmptyWeatherData).forGetter(ComplexEnvironment::weather),
                CloudData.CODEC.optionalFieldOf("cloud", EmptyCloudData).forGetter(ComplexEnvironment::cloud)
            ).apply(instance, ::ComplexEnvironment)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ComplexEnvironment> = composite(
                SkyData.STREAM_CODEC, ComplexEnvironment::sky,
                WeatherData.STREAM_CODEC, ComplexEnvironment::weather,
                CloudData.STREAM_CODEC, ComplexEnvironment::cloud,
            ::ComplexEnvironment)
    }
}
