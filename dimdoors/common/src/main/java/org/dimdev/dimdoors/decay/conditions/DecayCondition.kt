package org.dimdev.dimdoors.decay.conditions

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import net.minecraft.server.level.ServerLevel
import org.dimdev.dimcore.api.MapCodecHasHolder
import org.dimdev.dimdoors.decay.Decay
import org.dimdev.dimdoors.decay.DecaySource

interface DecayCondition : MapCodecHasHolder<DecayCondition> {
    fun test(level: ServerLevel, snapshot: Decay.Snapshot, source: DecaySource): Boolean

    companion object {
        @JvmField
        val CODEC: Codec<DecayCondition> = DecayConditions.codec

        @JvmField
        val LIST_CODEC: Codec<List<DecayCondition>> = Codec.either(CODEC, CODEC.listOf()).xmap(
            { either -> either.map({ listOf(it) }, { it }) },
            { conditions -> if (conditions.size > 1) Either.right(conditions) else Either.left(conditions[0]) }
        )

        @JvmField
        val NONE: DecayCondition = NoneDecayCondition
    }
}
