package org.dimdev.dimdoors.network.packet.s2c

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimdoors.DimensionalDoors.Companion.id
import org.dimdev.dimdoors.entity.mask.MaskType

data class MaskCatchAnimS2CPacket(val maskType: MaskType) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<MaskCatchAnimS2CPacket> {
        return TYPE
    }

    companion object {
        val ID: ResourceLocation = id("mask_catch_anim")
        val TYPE: CustomPacketPayload.Type<MaskCatchAnimS2CPacket> = CustomPacketPayload.Type<MaskCatchAnimS2CPacket>(ID)
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MaskCatchAnimS2CPacket> = MaskType.STREAM_CODEC.map(::MaskCatchAnimS2CPacket, MaskCatchAnimS2CPacket::maskType)
    }
}
