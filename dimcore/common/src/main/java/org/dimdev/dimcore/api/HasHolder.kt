package org.dimdev.dimcore.api

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec

interface HasHolder<T> {
    val type: T
}

interface MapCodecHasHolder<T> : HasHolder<MapCodec<out T>> {
    override val type: MapCodec<out T>

    fun <T : MapCodecHasHolder<T>> Codec<MapCodec<out T>>.codec(): Codec<T> = this.dispatch({it.type }, { it });
}

interface TypeHasHolder<T> : HasHolder<Type<T>> {
    override val type: Type<T>

    fun <T : TypeHasHolder<T>> Codec<Type<T>>.codec(): Codec<T> = this.dispatch({ it.type }, Type<T>::codec)
}

interface BuilderTypeHasHolder<T, V> : HasHolder<BuilderType<T, V>> {
    override val type: BuilderType<T, V>
}