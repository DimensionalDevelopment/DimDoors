package org.dimdev.dimdoors.world.decay.conditions

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import org.dimdev.dimcore.api.MapCodecHasHolder
import org.dimdev.dimdoors.world.decay.Decay

interface DecayCondition : MapCodecHasHolder<DecayCondition> {
    fun test(context: Decay.DecayContext): Boolean

    companion object {
        @JvmField
        val CODEC: Codec<DecayCondition> = DecayConditions.codec

        @JvmField
        val LIST_CODEC: Codec<List<DecayCondition>> = Codec.either(CODEC, CODEC.listOf()).xmap<List<DecayCondition>>(
            { either -> either.map<List<DecayCondition>>({ listOf(it) }, { it }) },
            { conditions -> if (conditions.size > 1) Either.right(conditions) else Either.left(conditions[0]) }
        )

        @JvmField
        val NONE: DecayCondition = NoneDecayCondition
    }
}
