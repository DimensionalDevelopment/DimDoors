package org.dimdev.dimdoors.api.util

import io.netty.buffer.ByteBuf
import net.minecraft.core.Registry
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import java.util.*
import kotlin.jvm.optionals.getOrNull

fun <B : ByteBuf, T : Any> StreamCodec<B, T>.nullable(): StreamCodec<B, T?> = ByteBufCodecs.optional(this).map<T?>(Optional<T>::getOrNull, Optional<T>::ofNullable)

val <T> ResourceKey<Registry<T>>.streamCodec: StreamCodec<ByteBuf, ResourceKey<T>> get() = ResourceKey.streamCodec(this)

fun <T: CustomPacketPayload> ResourceLocation.type(): CustomPacketPayload.Type<T> = CustomPacketPayload.Type<T>(this)
fun <T: CustomPacketPayload> String.type(): CustomPacketPayload.Type<T> = this.id().type()
