package org.dimdev.dimdoors.api.util

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level

class RotatedLocation(world: ResourceKey<Level>, pos: BlockPos, val yaw: Float, val pitch: Float) :
    Location(world, pos) {
    companion object {
        val CODEC = RecordCodecBuilder.create { instance -> instance.group(
                Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(RotatedLocation::worldId),
                BlockPos.CODEC.fieldOf("pos").forGetter(RotatedLocation::blockPos),
                Codec.FLOAT.fieldOf("yaw").forGetter(RotatedLocation::yaw),
                Codec.FLOAT.fieldOf("pitch").forGetter(RotatedLocation::pitch)
            ).apply(instance, ::RotatedLocation)
        }
        val STREAM_CODEC =
            StreamCodec.composite(
                ResourceKey.streamCodec(Registries.DIMENSION), RotatedLocation::worldId,
                BlockPos.STREAM_CODEC, RotatedLocation::blockPos,
                ByteBufCodecs.FLOAT, RotatedLocation::yaw,
                ByteBufCodecs.FLOAT, RotatedLocation::pitch,
                ::RotatedLocation
            )

    }
}
