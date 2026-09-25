package org.dimdev.dimdoors.network.packet

import io.netty.buffer.ByteBuf
import net.minecraft.core.Registry
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimdoors.DimensionalDoors.Companion.id

fun <T: CustomPacketPayload> ResourceLocation.type(): CustomPacketPayload.Type<T> = CustomPacketPayload.Type<T>(this)
fun <T: CustomPacketPayload> String.type(): CustomPacketPayload.Type<T> = this.id().type()
val <T> ResourceKey<Registry<T>>.streamCodec: StreamCodec<ByteBuf, ResourceKey<T>> get() = ResourceKey.streamCodec(this)