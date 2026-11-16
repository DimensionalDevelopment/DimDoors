package org.dimdev.dimdoors.item.door.condition

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import org.dimdev.dimdoors.blockentity.EntranceRiftBlockEntity

data class InverseCondition(val condition: Condition) : Condition {
    override val type get() = Conditions.INVERSE

    override fun matches(rift: EntranceRiftBlockEntity<*>) = !this.condition.matches(rift)

    companion object {
        val CODEC: MapCodec<InverseCondition> = RecordCodecBuilder.mapCodec { instance -> instance.group(
                Condition.CODEC.fieldOf("condition").forGetter(InverseCondition::condition)
            ).apply(instance, ::InverseCondition)
        }
    }
}
