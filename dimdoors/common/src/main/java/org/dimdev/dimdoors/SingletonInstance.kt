package org.dimdev.dimdoors

import com.mojang.serialization.MapCodec
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import org.dimdev.dimcore.api.cast

open class SingletonInstance<T> {
    val codec: MapCodec<T> = MapCodec.unit(this.cast())
    val streamCodec: StreamCodec<RegistryFriendlyByteBuf, T> = StreamCodec.unit<RegistryFriendlyByteBuf, T>(this.cast())
}
