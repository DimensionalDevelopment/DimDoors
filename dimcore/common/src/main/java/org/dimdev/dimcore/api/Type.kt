package org.dimdev.dimcore.api

import com.mojang.serialization.MapCodec
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec

open class Type<out T>(open val codec: MapCodec<out T>, open val streamCodec: StreamCodec<RegistryFriendlyByteBuf, out T>? = null) {
    val isSyncable = streamCodec != null
}

class BuilderType<out T, out V>(override val codec: MapCodec<out T>, val builderCodec: MapCodec<out V>, override val streamCodec: StreamCodec<RegistryFriendlyByteBuf, out T>?) : Type<T>(codec, streamCodec)