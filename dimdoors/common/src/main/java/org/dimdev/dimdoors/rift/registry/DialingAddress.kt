package org.dimdev.dimdoors.rift.registry

import com.mojang.serialization.Codec
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import java.util.function.Function

data class DialingAddress(@JvmField val dial1: Byte, @JvmField val dial2: Byte, @JvmField val dial3: Byte) {
    fun to(): Int {
        return (((dial1.toInt() and 0xFF) shl 16)
                or ((dial2.toInt() and 0xFF) shl 8)
                or (dial3.toInt() and 0xFF))
    }

    fun turnDial(dial: DialType): DialingAddress {
        return when (dial) {
            DialType.FIRST -> DialingAddress(dial1.wrap(), dial2, dial3)
            DialType.SECOND -> DialingAddress(dial1, dial2.wrap(), dial3)
            DialType.THIRD -> DialingAddress(dial1, dial2, dial3.wrap())
        }
    }

    enum class DialType {
        FIRST, SECOND, THIRD
    }

    companion object {
        val DEFAULT: DialingAddress = DialingAddress(0.toByte(), 0.toByte(), 0.toByte())
        val CODEC = Codec.INT.xmap(::from, DialingAddress::to)
        val STRING_CODEC: Codec<DialingAddress> = Codec.STRING.xmap(Integer::decode, Int::toString).xmap(DialingAddress::from, DialingAddress::to)
        val MAP_CODEC = CODEC.optionalFieldOf("address", DEFAULT)
        val STREAM_CODEC = ByteBufCodecs.INT.map (::from, DialingAddress::to).cast<RegistryFriendlyByteBuf>()

        fun from(value: Int): DialingAddress {
            return DialingAddress(
                (value shr 16).toByte(),
                (value shr 8).toByte(),
                value.toByte()
            )
        }

        private fun Byte.wrap(): Byte = ((this + 1) % 10).toByte()
    }
}
