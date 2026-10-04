package org.dimdev.dimdoors.world.pocket.type.addon.environment.sky

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.world.pocket.type.addon.environment.Environment
import org.dimdev.dimdoors.world.pocket.type.addon.environment.Environments
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.CloudData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.OverworldCloudData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.weather.OverworldWeatherData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.weather.WeatherData

class OverworldEnvironment(
    val dayTime: Long,
    val moonPhase: Int,
    val skyColor: Vec3,
    val rainLevel: Float,
    val precipitation: Biome.Precipitation,
    val thunderLevel: Float,
    val cloudHeight: Float,
    val cloudColor: Vec3
) : Environment {

    private val sunriseColors: FloatArray = FloatArray(4)

    private val skyData = OverworldSkyDataImpl()
    private val cloudData = OverworldCloudDataImpl()
    private val weatherData = OverworldWeatherDataImpl()

    override val sky: SkyData get() = skyData

    override val cloud: CloudData get() = cloudData
    override val weather: WeatherData get() = weatherData

    override val type get() = Environments.OVERWORLD

    private inner class OverworldSkyDataImpl : OverWorldSkyData {
        override val dayTime: Long get() = this@OverworldEnvironment.dayTime
        override val moonPhase: Int get() = this@OverworldEnvironment.moonPhase
        override val sunriseColors: FloatArray get() = this@OverworldEnvironment.sunriseColors
        override val skyColor: Vec3 get() = this@OverworldEnvironment.skyColor
        override val rainLevel: Float get() = this@OverworldEnvironment.rainLevel
        override val thunderLevel: Float get() = this@OverworldEnvironment.thunderLevel
    }

    private inner class OverworldCloudDataImpl : OverworldCloudData {
        override val cloudHeight: Float get() = this@OverworldEnvironment.cloudHeight
        override val cloudColor: Vec3 get() = this@OverworldEnvironment.cloudColor
    }

    private inner class OverworldWeatherDataImpl : OverworldWeatherData {
        override val precepitation: Biome.Precipitation get() = this@OverworldEnvironment.precipitation
        override val rainLevel: Float get() = this@OverworldEnvironment.rainLevel
    }

    companion object {
        val CODEC: MapCodec<OverworldEnvironment> = RecordCodecBuilder.mapCodec { instance-> instance.group(
                    Codec.LONG.optionalFieldOf("day_time", 12000L).forGetter(OverworldEnvironment::dayTime),
                    Codec.INT.optionalFieldOf("moon_phase", 0).forGetter(OverworldEnvironment::moonPhase),
                    Vec3.CODEC.optionalFieldOf("skyColor", Vec3(0.486, 0.654, 1.0)).forGetter(OverworldEnvironment::skyColor),
                    Codec.FLOAT.optionalFieldOf("rain_level", 0.0f).forGetter(OverworldEnvironment::rainLevel),
                    Biome.Precipitation.CODEC.optionalFieldOf("precipitation", Biome.Precipitation.NONE).forGetter(OverworldEnvironment::precipitation),
                    Codec.FLOAT.optionalFieldOf("thunder_level", 0.0f).forGetter(OverworldEnvironment::thunderLevel),
                    Codec.FLOAT.optionalFieldOf("cloud_height", 128f).forGetter(OverworldEnvironment::cloudHeight),
                    Vec3.CODEC.optionalFieldOf("cloud_color", Vec3(1.0, 1.0, 1.0)).forGetter(OverworldEnvironment::cloudColor)
                ).apply(instance, ::OverworldEnvironment)
            }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, OverworldEnvironment> =
            object : StreamCodec<RegistryFriendlyByteBuf, OverworldEnvironment> {
                override fun decode(buf: RegistryFriendlyByteBuf): OverworldEnvironment {
                    return OverworldEnvironment(
                        buf.readVarLong(),
                        buf.readVarInt(),
                        buf.readVec3(),
                        buf.readFloat(),
                        buf.readEnum(Biome.Precipitation::class.java),
                        buf.readFloat(),
                        buf.readFloat(),
                        buf.readVec3()
                    )
                }

                override fun encode(buf: RegistryFriendlyByteBuf, data: OverworldEnvironment) {
                    buf.writeVarLong(data.dayTime)
                    buf.writeVarInt(data.moonPhase)
                    buf.writeVec3(data.skyColor)
                    buf.writeFloat(data.rainLevel)
                    buf.writeEnum(data.precipitation)
                    buf.writeFloat(data.thunderLevel)
                    buf.writeFloat(data.cloudHeight)
                    buf.writeVec3(data.cloudColor)
                }
            }
    }
}
