package org.dimdev.dimdoors.criteria

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancements.critereon.ContextAwarePredicate
import net.minecraft.advancements.critereon.EntityPredicate
import net.minecraft.advancements.critereon.SimpleCriterionTrigger
import net.minecraft.server.level.ServerPlayer
import java.util.*
import java.util.function.Predicate

class PocketSpawnPointSetCondition : SimpleCriterionTrigger<PocketSpawnPointSetCondition.TriggerInstance>() {
    override fun codec(): Codec<TriggerInstance> {
        return TriggerInstance.Companion.CODEC
    }

    fun trigger(player: ServerPlayer) {
        this.trigger(player, Predicate { t: TriggerInstance? -> true })
    }

    data class TriggerInstance(val player: Optional<ContextAwarePredicate>) : SimpleInstance {
        override fun player() = player

        companion object {
            val CODEC: Codec<TriggerInstance> = RecordCodecBuilder.create { instance -> instance.group(
                    EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter { it.player }
                ).apply(instance, ::TriggerInstance)
            }
        }
    }

    companion object {
        const val ID: String = "pocket_spawn_point_set"
    }
}
