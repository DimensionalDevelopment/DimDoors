package org.dimdev.dimdoors.item.door.data.condition

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity

class AllCondition(conditions: List<Condition>) : MultipleCondition(conditions) {
    override val type: Holder<out MapCodec<Condition>>
        get() = Conditions.ALL

    override fun matches(rift: EntranceRiftBlockEntity<*>) = this.conditions.any { it.matches(rift) }

    companion object {
        var CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group(LIST.forGetter(AllCondition::conditions)).apply(instance, ::AllCondition) }
    }
}
