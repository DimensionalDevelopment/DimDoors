package org.dimdev.dimcore.api.ext

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import java.util.*
import kotlin.jvm.optionals.getOrNull

fun <B : ByteBuf, T : Any> StreamCodec<B, T>.nullable(): StreamCodec<B, T?> = ByteBufCodecs.optional(this).map<T?>(Optional<T>::getOrNull, Optional<T>::ofNullable)
