package org.dimdev.dimcore.api.errata.session

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.StreamCodec
import org.dimdev.dimcore.api.errata.network.SchemaStreamCodecs
import org.dimdev.dimcore.api.ext.nullable

data class SchemaError(val path: List<String>?, val message: Component) {
    companion object {
        @JvmField val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SchemaError> = StreamCodec.composite(
            SchemaStreamCodecs.PATH.nullable(), SchemaError::path,
            ComponentSerialization.STREAM_CODEC, SchemaError::message,
            ::SchemaError
        )
    }
}
