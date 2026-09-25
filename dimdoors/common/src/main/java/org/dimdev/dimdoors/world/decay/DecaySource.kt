package org.dimdev.dimdoors.world.decay

import com.mojang.serialization.Codec
import net.minecraft.util.StringRepresentable
import java.util.*

enum class DecaySource(private val serialized: String, private val decayIntoWorldThread: Boolean) : StringRepresentable {
    LIMBO("unravelled_fabric", false),
    REALITY_SPONGE("reality_sponge", false),
    RIFT("rift", true),
    CUSTOM("custom", false);

    override fun getSerializedName(): String {
        return serialized
    }

    fun decayIntoWorldThread(): Boolean {
        return decayIntoWorldThread
    }

    companion object {
        @JvmField
        val CODEC: Codec<DecaySource> = StringRepresentable.fromValues { entries.toTypedArray() }

        private val MAP = mutableMapOf<String, DecaySource>() //TODO: Remove once converted into codec.

        init {
            for (source in entries) MAP[source.serializedName] = source
        }

        fun fromName(name: String): DecaySource {
            return MAP.getOrDefault(name.lowercase(Locale.getDefault()), DecaySource.CUSTOM)
        }
    }
}
