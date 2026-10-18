package org.dimdev.dimcore.api.errata.network

import io.netty.buffer.ByteBuf
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimcore.api.errata.reference.ReferenceEntry
import org.dimdev.dimcore.api.errata.schema.ResolvedSchema
import org.dimdev.dimcore.api.errata.session.SchemaError
import org.dimdev.dimcore.api.ext.nullable

private fun <T : CustomPacketPayload> schemaPacket(name: String) = CustomPacketPayload.Type<T>(ResourceLocation.fromNamespaceAndPath("dimcore", name))

object SchemaStreamCodecs {
    @JvmField val PATH: StreamCodec<ByteBuf, List<String>> = ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list())

    @JvmField val OPTIONS: StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, List<ReferenceEntry>>> = linkedMap(ResourceLocation.STREAM_CODEC, ReferenceEntry.STREAM_CODEC.apply(ByteBufCodecs.list()))

    @JvmField val LABELS: StreamCodec<RegistryFriendlyByteBuf, Map<List<String>, Component>> = linkedMap(PATH, ComponentSerialization.STREAM_CODEC)

    @JvmField val INPUTS: StreamCodec<ByteBuf, Map<List<String>, Tag>> = linkedMap(PATH, ByteBufCodecs.TAG)

    internal fun <B : ByteBuf, K, V> linkedMap(key: StreamCodec<in B, K>, value: StreamCodec<in B, V>): StreamCodec<B, Map<K, V>> = ByteBufCodecs.map({ LinkedHashMap<K, V>(it) }, key, value)
}

data class SchemaState(val value: CompoundTag, val labels: Map<List<String>, Component>, val inputs: Map<List<String>, Tag>) {
    companion object {
        @JvmField val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SchemaState> = StreamCodec.composite(
            ByteBufCodecs.TRUSTED_COMPOUND_TAG, SchemaState::value,
            SchemaStreamCodecs.LABELS, SchemaState::labels,
            SchemaStreamCodecs.INPUTS, SchemaState::inputs,
            ::SchemaState
        )
    }
}

data class SessionOpenS2C(
    val id: Int,
    val title: Component,
    val schema: ResolvedSchema,
    val options: Map<ResourceLocation, List<ReferenceEntry>>,
    val state: SchemaState
) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<SessionOpenS2C> = TYPE

    companion object {
        @JvmField val TYPE = schemaPacket<SessionOpenS2C>("session_open")

        @JvmField val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SessionOpenS2C> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SessionOpenS2C::id,
            ComponentSerialization.STREAM_CODEC, SessionOpenS2C::title,
            ResolvedSchema.STREAM_CODEC, SessionOpenS2C::schema,
            SchemaStreamCodecs.OPTIONS, SessionOpenS2C::options,
            SchemaState.STREAM_CODEC, SessionOpenS2C::state,
            ::SessionOpenS2C
        )
    }
}

data class SessionAppliedS2C(val id: Int, val state: SchemaState, val errors: List<SchemaError>) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<SessionAppliedS2C> = TYPE

    companion object {
        @JvmField val TYPE = schemaPacket<SessionAppliedS2C>("session_applied")

        @JvmField val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SessionAppliedS2C> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SessionAppliedS2C::id,
            SchemaState.STREAM_CODEC, SessionAppliedS2C::state,
            SchemaError.STREAM_CODEC.apply(ByteBufCodecs.list()), SessionAppliedS2C::errors,
            ::SessionAppliedS2C
        )
    }
}

data class SessionResolvedS2C(val id: Int, val path: List<String>, val entry: ReferenceEntry?, val input: Tag?, val error: Component?) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<SessionResolvedS2C> = TYPE

    companion object {
        @JvmField val TYPE = schemaPacket<SessionResolvedS2C>("session_resolved")

        @JvmField val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SessionResolvedS2C> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SessionResolvedS2C::id,
            SchemaStreamCodecs.PATH, SessionResolvedS2C::path,
            ReferenceEntry.STREAM_CODEC.nullable(), SessionResolvedS2C::entry,
            ByteBufCodecs.TAG.nullable(), SessionResolvedS2C::input,
            ComponentSerialization.STREAM_CODEC.nullable(), SessionResolvedS2C::error,
            ::SessionResolvedS2C
        )
    }
}

data class SessionClosedS2C(val id: Int, val reason: Component) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<SessionClosedS2C> = TYPE

    companion object {
        @JvmField val TYPE = schemaPacket<SessionClosedS2C>("session_closed")

        @JvmField val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SessionClosedS2C> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SessionClosedS2C::id,
            ComponentSerialization.STREAM_CODEC, SessionClosedS2C::reason,
            ::SessionClosedS2C
        )
    }
}

data class SessionApplyC2S(val id: Int, val value: CompoundTag) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<SessionApplyC2S> = TYPE

    companion object {
        @JvmField val TYPE = schemaPacket<SessionApplyC2S>("session_apply")

        @JvmField val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SessionApplyC2S> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SessionApplyC2S::id,
            ByteBufCodecs.COMPOUND_TAG, SessionApplyC2S::value,
            ::SessionApplyC2S
        )
    }
}

data class SessionResolveC2S(val id: Int, val path: List<String>, val source: ResourceLocation, val input: Tag) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<SessionResolveC2S> = TYPE

    companion object {
        @JvmField val TYPE = schemaPacket<SessionResolveC2S>("session_resolve")

        @JvmField val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SessionResolveC2S> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SessionResolveC2S::id,
            SchemaStreamCodecs.PATH, SessionResolveC2S::path,
            ResourceLocation.STREAM_CODEC, SessionResolveC2S::source,
            ByteBufCodecs.TAG, SessionResolveC2S::input,
            ::SessionResolveC2S
        )
    }
}

data class SessionCloseC2S(val id: Int) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<SessionCloseC2S> = TYPE

    companion object {
        @JvmField val TYPE = schemaPacket<SessionCloseC2S>("session_close")

        @JvmField val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SessionCloseC2S> = StreamCodec.composite(ByteBufCodecs.VAR_INT, SessionCloseC2S::id, ::SessionCloseC2S)
    }
}
