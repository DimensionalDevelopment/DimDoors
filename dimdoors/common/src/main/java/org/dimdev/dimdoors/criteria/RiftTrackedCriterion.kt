package org.dimdev.dimdoors.criteria

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancements.critereon.ContextAwarePredicate
import net.minecraft.advancements.critereon.EntityPredicate
import net.minecraft.advancements.critereon.SimpleCriterionTrigger
import net.minecraft.server.level.ServerPlayer
import java.util.*

class RiftTrackedCriterion : SimpleCriterionTrigger<RiftTrackedCriterion.TriggerInstance>() {
    fun trigger(player: ServerPlayer) {
        this.trigger(player) { true }
    }

    override fun codec() = TriggerInstance.Companion.CODEC

    @JvmRecord
    data class TriggerInstance(val player: Optional<ContextAwarePredicate>) : SimpleInstance {
        override fun player(): Optional<ContextAwarePredicate> = player

        companion object {
            val CODEC: Codec<TriggerInstance> = RecordCodecBuilder.create { instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter { it.player }
                    ).apply(instance, ::TriggerInstance)
            }
        }
    }

    companion object {
        const val ID: String = "rift_tracked"
    }
}
