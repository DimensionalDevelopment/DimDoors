package org.dimdev.dimdoors.world.decay.pattern

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceKey
import org.dimdev.dimdoors.world.decay.Decay
import org.dimdev.dimdoors.world.decay.conditions.Applicator
import org.dimdev.dimdoors.world.decay.conditions.DecayCondition
import org.dimdev.dimdoors.world.decay.results.DecayResult
import java.util.stream.Stream

data class CompoundDecayPattern(val conditions: List<DecayCondition>, val result: DecayResult) : DecayPattern {
    override val type get() = DecayPatterns.COMPOUND

    override fun test(context: Decay.DecayContext): Boolean = conditions.all { condition -> condition.test(context) }

    override fun process(context: Decay.DecayContext): Int = result.process(context)

    override fun constructApplicable(access: RegistryAccess): Stream<ResourceKey<*>> =
        conditions.filterIsInstance<Applicator<*>>().stream().flatMap<ResourceKey<*>> { it.constructApplicable(access) }

    class Builder internal constructor() : DecayPattern.Builder<CompoundDecayPattern> {
        private val conditions = mutableListOf<DecayCondition>()
        private var result: DecayResult? = null

        fun condition(condition: DecayCondition): Builder {
            this.conditions.add(condition)
            return this
        }

        fun conditions(vararg conditions: DecayCondition): Builder {
            this.conditions.addAll(conditions)
            return this
        }

        fun conditions(conditions: List<DecayCondition>): Builder {
            this.conditions.addAll(conditions)
            return this
        }

        fun result(result: DecayResult): Builder {
            this.result = result
            return this
        }

        override fun build(provider: HolderLookup.Provider?): CompoundDecayPattern {
            val result = this.result ?: throw IllegalStateException("DecayResult must be set before building")
            if (conditions.isEmpty()) {
                throw IllegalStateException("At least one DecayCondition must be added before building")
            }
            return CompoundDecayPattern(conditions.toList(), result)
        }
    }

    companion object {
        const val KEY: String = "compound"

        @JvmField
        val CODEC: MapCodec<CompoundDecayPattern> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                DecayCondition.LIST_CODEC.fieldOf("conditions").forGetter(CompoundDecayPattern::conditions),
                DecayResult.CODEC.fieldOf("result").forGetter(CompoundDecayPattern::result)
            ).apply(instance, ::CompoundDecayPattern)
        }

        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
