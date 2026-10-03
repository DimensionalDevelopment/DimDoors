package org.dimdev.dimdoors.network.packet.s2c

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import org.dimdev.dimdoors.api.util.type
import org.dimdev.dimdoors.util.StreamCodecUtils.Companion.intArray

data class PortalColorsS2CPacket(val colors: IntArray) : CustomPacketPayload {
    override fun type() = TYPE

    companion object {
        val STREAM_CODEC = intArray(16).map(::PortalColorsS2CPacket, PortalColorsS2CPacket::colors).cast<RegistryFriendlyByteBuf>()
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
