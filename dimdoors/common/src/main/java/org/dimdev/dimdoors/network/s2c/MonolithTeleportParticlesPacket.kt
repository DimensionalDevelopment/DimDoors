package org.dimdev.dimdoors.network.s2c

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import org.dimdev.dimdoors.util.ext.type

object MonolithTeleportParticlesPacket : CustomPacketPayload {
    override fun type() = TYPE

    val TYPE = "monolith_tp_particles".type<MonolithTeleportParticlesPacket>()
    val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MonolithTeleportParticlesPacket> = StreamCodec.unit<RegistryFriendlyByteBuf, MonolithTeleportParticlesPacket>(MonolithTeleportParticlesPacket).cast()
}
