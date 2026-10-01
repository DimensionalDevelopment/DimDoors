package org.dimdev.dimdoors.particle.client

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import org.dimdev.dimdoors.particle.ModParticleTypes
import java.util.function.BiFunction
import java.util.function.Function

@JvmRecord
data class RiftParticleOptions(val color: Float, val averageAge: Int) : ParticleOptions {
    override fun getType(): ParticleType<*> {
        return ModParticleTypes.RIFT
    }

    public override fun color(): Float {
        return color
    }

    public override fun averageAge(): Int {
        return averageAge
    }

    companion object {
        fun of(isOutsidePocket: Boolean): RiftParticleOptions? {
            return if (isOutsidePocket) OUTSIDE else INSIDE
        }

        fun of(isOutsidePocket: Boolean, stablized: Boolean): RiftParticleOptions {
            if (isOutsidePocket) {
                if (stablized) {
                    return OUTSIDE_STABLE
                } else {
                    return OUTSIDE_UNSTABLE
                }
            } else {
                if (stablized) {
                    return INSIDE_STABLE
                } else {
                    return INSIDE_UNSTABLE
                }
            }
        }

        private val OUTSIDE = RiftParticleOptions(0.4f, 2000)
        private val INSIDE = RiftParticleOptions(0.8f, 2000)
        private val OUTSIDE_UNSTABLE = RiftParticleOptions(0.0f, 2000)
        private val INSIDE_UNSTABLE = RiftParticleOptions(0.7f, 2000)
        private val OUTSIDE_STABLE = RiftParticleOptions(0.0f, 750)
        private val INSIDE_STABLE = RiftParticleOptions(0.7f, 750)

        val CODEC: MapCodec<RiftParticleOptions?> =
            RecordCodecBuilder.mapCodec<RiftParticleOptions?>(Function { instance: RecordCodecBuilder.Instance<RiftParticleOptions?>? ->
                instance!!.group<Float?, Int?>(
                    Codec.FLOAT.fieldOf("color")
                        .forGetter<RiftParticleOptions?>(Function { obj: RiftParticleOptions? -> obj!!.color() }),
                    Codec.INT.fieldOf("averageAge")
                        .forGetter<RiftParticleOptions?>(Function { obj: RiftParticleOptions? -> obj!!.averageAge() })
                )
                    .apply<RiftParticleOptions?>(
                        instance,
                        BiFunction { color: Float?, averageAge: Int? -> RiftParticleOptions(color!!, averageAge!!) })
            })

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf?, RiftParticleOptions?> =
            StreamCodec.composite<RegistryFriendlyByteBuf?, RiftParticleOptions?, Float?, Int?>(
                ByteBufCodecs.FLOAT,
                Function { obj: RiftParticleOptions? -> obj!!.color() },
                ByteBufCodecs.VAR_INT,
                Function { obj: RiftParticleOptions? -> obj!!.averageAge() },
                BiFunction { color: Float?, averageAge: Int? -> RiftParticleOptions(color!!, averageAge!!) })
    }
}
