package org.dimdev.dimdoors.world.pocket.type.addon.sky

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.OverWorldSkyData

class OverWorldSkyDataImpl(
    override var dayTime: Long,
    var moonPhaseBacking: Int,
    override val skyColor: Vec3,
    override val rainLevel: Float,
    override val thunderLevel: Float
) : OverWorldSkyData {
    override val sunriseColors: FloatArray = FloatArray(4)

    override var moonPhase: Int
        get() = moonPhaseBacking % 8
        set(value) { moonPhaseBacking = value }

    companion object {
        val CODEC: MapCodec<OverWorldSkyData> = RecordCodecBuilder.mapCodec { instance -> instance.group(
                Codec.LONG.optionalFieldOf("day_time", 12000L).forGetter(OverWorldSkyData::dayTime),
                Codec.INT.optionalFieldOf("moon_phase", 0).forGetter(OverWorldSkyData::moonPhase),
                Vec3.CODEC.optionalFieldOf("skyColor", Vec3(0.486, 0.654, 1.0)).forGetter(OverWorldSkyData::skyColor),
                Codec.FLOAT.optionalFieldOf("rain_level", 0.0f).forGetter(OverWorldSkyData::rainLevel),
                Codec.FLOAT.optionalFieldOf("thunder_level", 0.0f).forGetter(OverWorldSkyData::thunderLevel)
            ).apply(instance, ::OverWorldSkyDataImpl)
        }

        val STREAM_CODEC = object : StreamCodec<RegistryFriendlyByteBuf, OverWorldSkyData> {
                override fun decode(buf: RegistryFriendlyByteBuf): OverWorldSkyData {
                    return OverWorldSkyDataImpl(
                        buf.readVarLong(),
                        buf.readVarInt(),
                        buf.readVec3(),
                        buf.readFloat(),
                        buf.readFloat()
                    )
                }

                override fun encode(buf: RegistryFriendlyByteBuf, data: OverWorldSkyData) {
                    buf.writeVarLong(data.dayTime)
                    buf.writeVarInt(data.moonPhase)
                    buf.writeVec3(data.skyColor)
                    buf.writeFloat(data.rainLevel)
                    buf.writeFloat(data.thunderLevel)
                }
            }
    }
}
