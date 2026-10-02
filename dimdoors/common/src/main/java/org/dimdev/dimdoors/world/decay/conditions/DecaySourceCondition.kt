package org.dimdev.dimdoors.world.decay.conditions

import com.mojang.serialization.codecs.RecordCodecBuilder
import org.dimdev.dimdoors.world.decay.Decay
import org.dimdev.dimdoors.world.decay.DecaySource

data class DecaySourceCondition(val source: DecaySource) : DecayCondition {
    override val type get() = DecayConditions.DECAY_SOURCE

    override fun test(context: Decay.DecayContext): Boolean {
        return this.source == context.source
    }

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group<DecaySource>(
                    DecaySource.CODEC.fieldOf("source").forGetter<DecaySourceCondition>(DecaySourceCondition::source)
                ).apply(instance, ::DecaySourceCondition)
            }
        var KEY: String = "decay_source"
    }
}
