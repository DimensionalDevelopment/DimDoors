package org.dimdev.dimdoors.item.door.data.condition

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import java.util.function.Function

class AnyCondition(conditions: List<Condition>) : MultipleCondition(conditions) {
    override val type get() = Conditions.ANY

    override fun matches(rift: EntranceRiftBlockEntity<*>) = this.conditions.any { it.matches(rift) }

    companion object {
        var CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group(LIST.forGetter(MultipleCondition::conditions)).apply(instance, ::AnyCondition) }
    }
}
