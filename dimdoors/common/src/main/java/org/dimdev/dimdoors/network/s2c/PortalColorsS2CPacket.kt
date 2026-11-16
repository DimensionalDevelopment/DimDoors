package org.dimdev.dimdoors.network.s2c

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import org.dimdev.dimdoors.util.codec.StreamCodecUtils.Companion.intArray
import org.dimdev.dimdoors.util.ext.type

data class PortalColorsS2CPacket(val colors: IntArray) : CustomPacketPayload {
    override fun type() = TYPE

    companion object {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, PortalColorsS2CPacket> = intArray(16).map(::PortalColorsS2CPacket, PortalColorsS2CPacket::colors).cast()
        val TYPE = "portal_colors".type<PortalColorsS2CPacket>()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PortalColorsS2CPacket

        return colors.contentEquals(other.colors)
    }

    override fun hashCode(): Int {
        return colors.contentHashCode()
    }
}
