package org.dimdev.dimdoors.particle.client

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import org.dimdev.dimdoors.particle.ModParticleTypes

@JvmRecord
data class RiftParticleOptions(val color: Float, val averageAge: Int) : ParticleOptions {
    override fun getType(): ParticleType<*> {
        return ModParticleTypes.RIFT
    }

    companion object {
        fun of(isOutsidePocket: Boolean): RiftParticleOptions {
            return if (isOutsidePocket) OUTSIDE else INSIDE
        }

        fun of(isOutsidePocket: Boolean, stablized: Boolean) = if (isOutsidePocket) {
            if (stablized) {
                OUTSIDE_STABLE
            } else {
                OUTSIDE_UNSTABLE
            }
        } else {
            if (stablized) {
                INSIDE_STABLE
            } else {
                INSIDE_UNSTABLE
            }
        }

        private val OUTSIDE = RiftParticleOptions(0.4f, 2000)
        private val INSIDE = RiftParticleOptions(0.8f, 2000)
        private val OUTSIDE_UNSTABLE = RiftParticleOptions(0.0f, 2000)
        private val INSIDE_UNSTABLE = RiftParticleOptions(0.7f, 2000)
        private val OUTSIDE_STABLE = RiftParticleOptions(0.0f, 750)
        private val INSIDE_STABLE = RiftParticleOptions(0.7f, 750)

        val CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group(
                Codec.FLOAT.fieldOf("color").forGetter(RiftParticleOptions::color),
                Codec.INT.fieldOf("averageAge").forGetter(RiftParticleOptions::averageAge)
            ).apply(instance, ::RiftParticleOptions)
        }

        val STREAM_CODEC = StreamCodec.composite<RegistryFriendlyByteBuf, RiftParticleOptions, Float, Int>(
                ByteBufCodecs.FLOAT, RiftParticleOptions::color,
                ByteBufCodecs.VAR_INT, RiftParticleOptions::averageAge,
            ::RiftParticleOptions)
    }
}
