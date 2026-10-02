package org.dimdev.dimdoors.particle

import com.mojang.serialization.MapCodec
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import org.dimdev.dimdoors.particle.client.RiftParticleOptions

object RiftParticleType : ParticleType<RiftParticleOptions>(true) {
    override fun codec(): MapCodec<RiftParticleOptions> = RiftParticleOptions.CODEC

    override fun streamCodec(): StreamCodec<in RegistryFriendlyByteBuf, RiftParticleOptions> = RiftParticleOptions.STREAM_CODEC
}
