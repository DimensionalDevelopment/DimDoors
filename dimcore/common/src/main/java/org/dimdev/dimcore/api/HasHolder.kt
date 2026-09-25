package org.dimdev.dimcore.api

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.core.Holder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec

interface HasHolder<T> {
    val type: Holder<out T>
}

interface MapCodecHasHolder<T> : HasHolder<MapCodec<out T>> {
    override val type: Holder<out MapCodec<out T>>

    fun <T : MapCodecHasHolder<T>> Codec<Holder<out MapCodec<out T>>>.codec(): Codec<T> = this.dispatch({ it.type }, Holder<out MapCodec<out T>>::value);
}

interface TypeHasHolder<T> : HasHolder<Type<T>> {
    override val type: Holder<out Type<T>>

    fun <T : TypeHasHolder<T>> Codec<Holder<out Type<T>>>.codec(): Codec<T> = this.dispatch({ it.type }, { it.value().codec })
}

interface BuilderTypeHasHolder<T, V> : HasHolder<BuilderType<T, V>> {
    override val type: Holder<out BuilderType<T, V>>
}