package org.dimdev.dimdoors.world.decay.results

import com.mojang.datafixers.Products.P2
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import org.dimdev.dimcore.api.MapCodecHasHolder
import org.dimdev.dimdoors.world.decay.Decay

interface DecayResult : MapCodecHasHolder<DecayResult> {
    fun entropy(): Int = 0

    fun worldThreadChance(): Float = 0f

    fun process(context: Decay.DecayContext): Int

    fun produces(): List<Result>

    data class Result(val obj: Any, val amount: Int)

    companion object {
        @JvmStatic
        fun <T : DecayResult> entropyCodec(instance: RecordCodecBuilder.Instance<T>): P2<RecordCodecBuilder.Mu<T>, Int, Float> {
            return instance.group(
                Codec.INT.optionalFieldOf("entropy", 0).forGetter { it.entropy() },
                Codec.FLOAT.optionalFieldOf("world_thread_chance", 0.1f).forGetter { it.worldThreadChance() })
        }

        @JvmField
        val CODEC: Codec<DecayResult> = DecayResults.codec
    }
}
