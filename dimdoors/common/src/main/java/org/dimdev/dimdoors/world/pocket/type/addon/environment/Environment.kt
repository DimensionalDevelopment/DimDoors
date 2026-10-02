package org.dimdev.dimdoors.world.pocket.type.addon.environment

import com.mojang.serialization.Codec
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import org.dimdev.dimcore.api.TypeHasHolder

import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.CloudData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.SkyData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.weather.WeatherData


interface Environment : TypeHasHolder<Environment> {
    val sky: SkyData
    val cloud: CloudData
    val weather: WeatherData

    companion object {
        val CODEC: Codec<Environment> = Environments.codec
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Environment> = Environments.streamCodec
    }

}
