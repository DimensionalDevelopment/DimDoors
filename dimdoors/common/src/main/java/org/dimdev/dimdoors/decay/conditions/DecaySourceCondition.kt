package org.dimdev.dimdoors.decay.conditions

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import org.dimdev.dimdoors.decay.Decay
import org.dimdev.dimdoors.decay.DecaySource

data class DecaySourceCondition(val source: DecaySource) : DecayCondition {
    override val type get() = DecayConditions.DECAY_SOURCE

    override fun test(level: ServerLevel, snapshot: Decay.Snapshot, source: DecaySource): Boolean {
        return this.source == source
    }

    companion object {
        val CODEC: MapCodec<DecaySourceCondition> = RecordCodecBuilder.mapCodec { instance -> instance.group(
                    DecaySource.CODEC.fieldOf("source").forGetter(DecaySourceCondition::source)
                ).apply(instance, ::DecaySourceCondition)
            }
        var KEY: String = "decay_source"
    }
}
