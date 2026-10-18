package org.dimdev.dimcore.api.errata.schema

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec

class SchemaType<T : Any>(val streamCodec: StreamCodec<in RegistryFriendlyByteBuf, T>)
