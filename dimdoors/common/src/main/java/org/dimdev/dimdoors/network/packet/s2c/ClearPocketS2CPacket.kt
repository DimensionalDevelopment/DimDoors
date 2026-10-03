package org.dimdev.dimdoors.network.packet.s2c

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.util.type

object ClearPocketS2CPacket : CustomPacketPayload {
    override fun type() = TYPE

    val STREAM_CODEC = StreamCodec.unit<RegistryFriendlyByteBuf, ClearPocketS2CPacket>(ClearPocketS2CPacket)

    val TYPE = "clear_pocket".type<ClearPocketS2CPacket>()
}
