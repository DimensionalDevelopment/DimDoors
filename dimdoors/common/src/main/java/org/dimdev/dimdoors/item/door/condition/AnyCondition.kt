package org.dimdev.dimdoors.item.door.condition

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import org.dimdev.dimdoors.blockentity.EntranceRiftBlockEntity

class AnyCondition(conditions: List<Condition>) : MultipleCondition(conditions) {
    override val type get() = Conditions.ANY

    override fun matches(rift: EntranceRiftBlockEntity<*>) = this.conditions.any { it.matches(rift) }

    companion object {
        val CODEC: MapCodec<AnyCondition> = RecordCodecBuilder.mapCodec { instance -> instance.group(LIST.forGetter(MultipleCondition::conditions)).apply(instance, ::AnyCondition) }
    }
}
