package org.dimdev.dimdoors.world.pocket.type.addon.environment.sky

import com.mojang.datafixers.util.Function8
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.Type
import org.dimdev.dimdoors.world.pocket.type.addon.environment.Environment
import org.dimdev.dimdoors.world.pocket.type.addon.environment.Environments
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.CloudData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud.OverworldCloudData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.weather.OverworldWeatherData
import org.dimdev.dimdoors.world.pocket.type.addon.environment.weather.WeatherData
import java.util.function.Function

class OverworldEnvironment(
    private val dayTime: Long,
    private val moonPhase: Int,
    private val skyColor: Vec3,
    private val rainLevel: Float,
    private val precipitation: Biome.Precipitation,
    private val thunderLevel: Float,
    internal val cloudHeight: Float,
    private val cloudColor: Vec3
) : Environment {

    private val sunriseColors: FloatArray = FloatArray(4)

    private val skyData: OverworldSkyDataImpl = OverworldEnvironment.OverworldSkyDataImpl()
    private val cloudData: OverworldCloudDataImpl = OverworldEnvironment.OverworldCloudDataImpl()
    private val weatherData: OverworldWeatherDataImpl = OverworldEnvironment.OverworldWeatherDataImpl()

    override val sky: SkyData get() = skyData

    override val cloud: CloudData get() = cloudData
    override val weather: WeatherData get() = weatherData

    override val type: Holder<out Type<Environment>> get() = Environments.OVERWORLD

    private inner class OverworldSkyDataImpl : OverWorldSkyData {
        override val dayTime: Long get() = this@OverworldEnvironment.dayTime

        override val moonPhase: Int get() = this@OverworldEnvironment.moonPhase

        override val sunriseColors: FloatArray get() = this@OverworldEnvironment.sunriseColors

        override val skyColor: Vec3 get() = this@OverworldEnvironment.skyColor

        override val rainLevel: Float get() = this@OverworldEnvironment.rainLevel

        override val thunderLevel: Float get() = this@OverworldEnvironment.thunderLevel
    }

    internal class OverworldCloudDataImpl : OverworldCloudData {
        override fun getCloudHeight(): Float {
            return this@OverworldEnvironment.cloudHeight
        }

        override fun getCloudColor(): Vec3 {
            return this@OverworldEnvironment.cloudColor
        }
    }

    private inner class OverworldWeatherDataImpl : OverworldWeatherData {
        override fun getPrecepitation(): Biome.Precipitation {
            return this@OverworldEnvironment.precipitation
        }

        override fun getRainLevel(): Float {
            return this@OverworldEnvironment.rainLevel
        }
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
