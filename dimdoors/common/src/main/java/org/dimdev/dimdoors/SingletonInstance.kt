package org.dimdev.dimdoors

import com.mojang.serialization.MapCodec
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import org.dimdev.dimcore.api.ext.cast

open class SingletonInstance<T : Any> {
    val codec: MapCodec<T> = MapCodec.unit(this.cast<T>())
    val streamCodec: StreamCodec<RegistryFriendlyByteBuf, T> = StreamCodec.unit(this.cast<T>())
}
