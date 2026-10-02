package org.dimdev.dimdoors.util

import io.netty.buffer.ByteBuf
import io.netty.handler.codec.EncoderException
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.sounds.Music
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.Vec3
import java.util.*
import kotlin.jvm.optionals.getOrNull

class StreamCodecUtils {

    companion object {
        val MUSIC: StreamCodec<RegistryFriendlyByteBuf, Music> =
            StreamCodec.composite(
                SoundEvent.STREAM_CODEC, Music::getEvent,
                ByteBufCodecs.VAR_INT, Music::getMinDelay,
                ByteBufCodecs.VAR_INT, Music::getMaxDelay,
                ByteBufCodecs.BOOL, Music::replaceCurrentMusic,
                ::Music
            )


        val VEC3: StreamCodec<ByteBuf, Vec3> = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, { obj -> obj.x() },
            ByteBufCodecs.DOUBLE, { obj -> obj.y() },
            ByteBufCodecs.DOUBLE, { obj -> obj.z() },
            ::Vec3)

        val BOUNDING_BOX: StreamCodec<ByteBuf, BoundingBox> = StreamCodec.composite(
                ByteBufCodecs.INT, { obj -> obj.minX() },
                ByteBufCodecs.INT, { obj -> obj.minY() },
                ByteBufCodecs.INT, { obj -> obj.minZ() },
                ByteBufCodecs.INT, { obj -> obj.maxX() },
                ByteBufCodecs.INT, { obj -> obj.maxY() },
                ByteBufCodecs.INT, { obj -> obj.maxZ() },
            ::BoundingBox)

        @JvmStatic
        fun intArray(length: Int) = object : StreamCodec<FriendlyByteBuf, IntArray> {
            override fun decode(buffer: FriendlyByteBuf): IntArray {
                return buffer.readVarIntArray(length)
            }

            override fun encode(buffer: FriendlyByteBuf, value: IntArray) {
                if (value.size > length) {
                    throw EncoderException("Array with size " + value.size + " is bigger than allowed " + length)
                } else {
                    buffer.writeVarIntArray(value)
                }
            }
        }
    }
}

fun <B : ByteBuf, T : Any> StreamCodec<B, T>.nullable(): StreamCodec<B, T?> = ByteBufCodecs.optional(this).map<T?>(Optional<T>::getOrNull, Optional<T>::ofNullable)