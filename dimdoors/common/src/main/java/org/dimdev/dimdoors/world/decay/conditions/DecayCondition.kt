package org.dimdev.dimdoors.world.decay.conditions

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.core.Holder
import org.dimdev.dimcore.api.MapCodecHasHolder
import org.dimdev.dimdoors.SingletonInstance
import org.dimdev.dimdoors.world.decay.Decay
import java.util.List
import java.util.function.Function

interface DecayCondition : MapCodecHasHolder<DecayCondition> {
    fun test(context: Decay.DecayContext): Boolean

    companion object {
        val CODEC: Codec<DecayCondition?> =
            DecayConditionType.CODEC.dispatch("type", { obj: DecayCondition? -> obj!!.type }, DecayConditionType::codec)
        @JvmField
        val LIST_CODEC: Codec<MutableList<DecayCondition?>?>? =
            Codec.either<DecayCondition?, MutableList<DecayCondition?>?>(
                CODEC, CODEC.listOf()
            ).xmap<MutableList<DecayCondition?>?>(
                Function { either: Either<DecayCondition?, MutableList<DecayCondition?>?>? ->
                    either!!.map<MutableList<DecayCondition?>?>(
                        Function { e1: DecayCondition? -> List.of(e1) },
                        Function.identity<MutableList<DecayCondition?>?>()
                    )
                },
                Function { conditions: MutableList<DecayCondition?>? ->
                    if (conditions!!.size > 1) Either.right<DecayCondition?, MutableList<DecayCondition?>?>(
                        conditions
                    ) else Either.left<DecayCondition?, MutableList<DecayCondition?>?>(conditions.get(0))
                })

        val NONE: DecayCondition = object : DecayCondition, SingletonInstance<DecayCondition>() {
            private val ID = "none"

            override val type get() = DecayConditions.NONE

            override fun test(context: Decay.DecayContext): Boolean {
                return false
            }
        }
    }
}
