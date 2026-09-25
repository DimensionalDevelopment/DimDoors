package org.dimdev.dimdoors.world.pocket.type.addon.environment.cloud

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.util.StreamCodecUtils

data class OverworldCloudDataImpl(override val cloudHeight: Float, override val cloudColor: Vec3) : OverworldCloudData {

    companion object {
        val CODEC: MapCodec<OverworldCloudData> = RecordCodecBuilder.mapCodec { instance -> instance.group(
                Codec.FLOAT.optionalFieldOf("height", 128f).forGetter(OverworldCloudData::cloudHeight),
                Vec3.CODEC.optionalFieldOf("color", Vec3(1.0, 1.0, 1.0)).forGetter(OverworldCloudData::cloudColor)
            ).apply(instance, ::OverworldCloudDataImpl)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, OverworldCloudData> = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            OverworldCloudData::cloudHeight,
            StreamCodecUtils.VEC3,
            OverworldCloudData::cloudColor,
            ::OverworldCloudDataImpl
        )

    }
}
