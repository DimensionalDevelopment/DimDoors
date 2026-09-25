package org.dimdev.dimdoors.item.door.data.condition

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import org.dimdev.dimdoors.api.util.math.Equation
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity

data class HeightCondition(val height: Equation) : Condition {
    override val type get() = Conditions.HEIGHT

    override fun matches(rift: EntranceRiftBlockEntity<*>) = this.height.asBoolean("height" to rift.blockPos.y.toDouble())

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group(
                Equation.CODEC.fieldOf("height").forGetter(HeightCondition::height)
            ).apply(instance, ::HeightCondition)
        }
    }
}