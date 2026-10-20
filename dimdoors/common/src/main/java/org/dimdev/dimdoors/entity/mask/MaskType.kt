package org.dimdev.dimdoors.entity.mask

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.RandomSource
import net.minecraft.util.StringRepresentable

enum class MaskType(private val serializedName: String, val detectionRange: Double, val sightHalfAngle: Double) : StringRepresentable {
    CYCLOP("cyclop", 6.0, 60.0),
    ECHO("echo", 4.0, 90.0),
    ENLIGHTENED("enlightened", 3.0, 180.0),
    FORESIGHT("foresight", 2.0, 90.0),
    SCULKING("sculking", 8.0, 0.0),
    BLACK("black", 50.0, 0.0),
    RANDOM("random", 6.0, 0.0);

    val isSighted get() = sightHalfAngle > 0

    override fun getSerializedName() = serializedName

    companion object {
        @JvmField val CODEC: StringRepresentable.EnumCodec<MaskType> = StringRepresentable.fromEnum { entries.toTypedArray() }

        @JvmField val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MaskType> = StreamCodec.of(RegistryFriendlyByteBuf::writeEnum) { byteBuf -> byteBuf.readEnum(MaskType::class.java) }

        val NATURAL = listOf(CYCLOP, ECHO, ENLIGHTENED, FORESIGHT, SCULKING)

        fun random(random: RandomSource) = NATURAL[random.nextInt(NATURAL.size)]
    }
}
