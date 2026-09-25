package org.dimdev.dimdoors.item.door.data.condition

import com.mojang.serialization.codecs.RecordCodecBuilder
import org.dimdev.dimdoors.api.util.math.Equation
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity

data class LightLevelCondition(val lightLevel: Equation) : Condition {
    override fun matches(rift: EntranceRiftBlockEntity<*>) =
        this.lightLevel.asBoolean("light" to rift.riftLevel.getLightEmission(rift.blockPos).toDouble())

    override val type get() = Conditions.LIGHT_LEVEL

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group(
                Equation.CODEC.fieldOf("light_level").forGetter(LightLevelCondition::lightLevel)
            ).apply(instance, ::LightLevelCondition)
        }
    }
}
