package org.dimdev.dimdoors.item.door.condition

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import org.dimdev.dimdoors.blockentity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.util.math.Equation

data class HeightCondition(val height: Equation) : Condition {
    override val type get() = Conditions.HEIGHT

    override fun matches(rift: EntranceRiftBlockEntity<*>) = this.height.asBoolean("height" to rift.blockPos.y.toDouble())

    companion object {
        val CODEC: MapCodec<HeightCondition> = RecordCodecBuilder.mapCodec { instance -> instance.group(
                Equation.CODEC.fieldOf("height").forGetter(HeightCondition::height)
            ).apply(instance, ::HeightCondition)
        }
    }
}