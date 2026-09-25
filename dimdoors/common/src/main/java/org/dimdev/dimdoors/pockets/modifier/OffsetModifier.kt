package org.dimdev.dimdoors.pockets.modifier

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Vec3i
import org.dimdev.dimdoors.api.util.math.Equation
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.world.pocket.type.Pocket

data class OffsetModifier(val offsetX: Equation, val offsetY: Equation, val offsetZ: Equation) : Modifier {
    override val type get() = Modifiers.OFFSET

    override fun apply(parameters: PocketGenerationContext, manager: RiftManager) {}

    override fun apply(parameters: PocketGenerationContext, builder: Pocket.PocketBuilder<*, *>) {
        val variableMap = parameters.toVariableMap(HashMap<String?, Double?>())
        builder.offsetOrigin(Vec3i(offsetX.apply(variableMap).toInt(), offsetY.apply(variableMap).toInt(), offsetZ.apply(variableMap).toInt()))
    }

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group(
                Equation.CODEC.optionalFieldOf("offsetX", Equation.ZERO).forGetter(OffsetModifier::offsetX),
                Equation.CODEC.optionalFieldOf("offsetY", Equation.ZERO).forGetter(OffsetModifier::offsetY),
                Equation.CODEC.optionalFieldOf("offsetZ", Equation.ZERO).forGetter(OffsetModifier::offsetZ)
            ).apply(instance, ::OffsetModifier)
        }

        const val KEY: String = "offset"
    }
}