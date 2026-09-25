package org.dimdev.dimdoors.network.packet.s2c

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import org.dimdev.dimdoors.network.packet.type

data class MonolithAggroParticlesPacket(val aggro: Int) : CustomPacketPayload {
    override fun type() = TYPE

    companion object {
        val TYPE = "monolith_aggro_particles".type<MonolithAggroParticlesPacket>()
        val STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                MonolithAggroParticlesPacket::aggro,
                ::MonolithAggroParticlesPacket
            ).cast<RegistryFriendlyByteBuf>()
    }
}
