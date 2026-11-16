package org.dimdev.dimdoors.item.door.condition

import com.mojang.serialization.codecs.RecordCodecBuilder
import org.dimdev.dimdoors.blockentity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.util.math.Equation

data class LightLevelCondition(val lightLevel: Equation) : Condition {
    override fun matches(rift: EntranceRiftBlockEntity<*>) =
        this.lightLevel.asBoolean("light" to rift.level!!.getLightEmission(rift.blockPos).toDouble())

    override val type get() = Conditions.LIGHT_LEVEL

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group(
                Equation.CODEC.fieldOf("light_level").forGetter(LightLevelCondition::lightLevel)
            ).apply(instance, ::LightLevelCondition)
        }
    }
}
