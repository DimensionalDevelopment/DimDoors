package org.dimdev.dimdoors.criteria

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancements.critereon.ContextAwarePredicate
import net.minecraft.advancements.critereon.EntityPredicate
import net.minecraft.advancements.critereon.SimpleCriterionTrigger
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import java.util.*

class TagBlockBreakCriteria : SimpleCriterionTrigger<TagBlockBreakCriteria.TriggerInstance>() {
    fun trigger(player: ServerPlayer, block: BlockState) = this.trigger(player) { c -> block.`is`(c.blockTagKey) }

    override fun codec() = TriggerInstance.CODEC

    @JvmRecord
    data class TriggerInstance(private val player: Optional<ContextAwarePredicate>, val blockTagKey: TagKey<Block>) : SimpleInstance {
        override fun player() = player

        companion object {
            val CODEC= RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<TriggerInstance> -> instance.group(
                    EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter { it.player },
                    TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(TriggerInstance::blockTagKey)
                ).apply(instance, ::TriggerInstance)
            }
        }
    }

    companion object {
        const val ID: String = "tag_block_break"
    }
}
