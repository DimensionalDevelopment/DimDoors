package org.dimdev.dimdoors.item.door.data.condition

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import java.util.function.Function

@JvmRecord
data class InverseCondition(val condition: Condition) : Condition {
    override val type get() = Conditions.INVERSE

    override fun matches(rift: EntranceRiftBlockEntity<*>) = !this.condition.matches(rift)

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group(
                Condition.CODEC.fieldOf("condition").forGetter(InverseCondition::condition)
            ).apply(instance, ::InverseCondition)
        }
    }
}
