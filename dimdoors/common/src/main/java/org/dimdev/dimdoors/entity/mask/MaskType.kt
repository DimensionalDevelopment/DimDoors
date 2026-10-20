package org.dimdev.dimdoors.entity.mask

import com.mojang.serialization.Codec
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.StringRepresentable

enum class MaskType(private val serializedName: String, val detectionRange: Double) : StringRepresentable {
    CYCLOP("cyclop", 6.0),
    ECHO("echo", 4.0),
    ENLIGHTENED("enlightened", 3.0),
    FORESIGHT("foresight", 2.0),
    SCULKING("sculking", 8.0),
    BLACK("black", 50.0),
    RANDOM("random", 6.0);

    override fun getSerializedName() = serializedName

    companion object {
        val CODEC: Codec<MaskType> = StringRepresentable.fromValues { entries.toTypedArray() }

        @JvmField val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MaskType> = StreamCodec.of(RegistryFriendlyByteBuf::writeEnum) { byteBuf -> byteBuf.readEnum(MaskType::class.java) }
    }
}
