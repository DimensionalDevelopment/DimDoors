package org.dimdev.dimdoors.pockets.virtual.selection

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import org.dimdev.dimdoors.api.util.math.Equation
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.pockets.virtual.VirtualPockets
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference
import org.dimdev.dimdoors.api.util.mutableList
import org.dimdev.dimdoors.world.pocket.type.Pocket

class ConditionalSelector(val pocketMap: MutableList<ConditionalPocket>) : ImplementedVirtualPocket<ConditionalSelector> {
    override fun prepareAndPlacePocket(parameters: PocketGenerationContext, setupLoot: Boolean?): Pocket<*, *>? {
        return getNextPocket(parameters).prepareAndPlacePocket(parameters, setupLoot)
    }

    override fun prepareAndPlacePocket(parameters: PocketGenerationContext): Pocket<*, *>? {
        return getNextPocket(parameters).prepareAndPlacePocket(parameters)
    }

    override fun getNextPocketGeneratorReference(parameters: PocketGenerationContext): PocketGeneratorReference<*> {
        return getNextPocket(parameters).getNextPocketGeneratorReference(parameters)
    }

    override fun peekNextPocketGeneratorReference(parameters: PocketGenerationContext): PocketGeneratorReference<*> {
        return getNextPocket(parameters).peekNextPocketGeneratorReference(parameters)
    }

    override val type get() = VirtualPockets.CONDITIONAL_SELECTOR

    override fun getWeight(parameters: PocketGenerationContext): Double {
        return getNextPocket(parameters).getWeight(parameters)
    }

    private fun getNextPocket(parameters: PocketGenerationContext): VirtualPocket {
        val map = parameters.toVariableMap(mutableMapOf())
        return pocketMap.firstOrNull { it.condition.asBoolean(map) }?.pocket ?: ImplementedVirtualPocket.NoneVirtualPocket.NONE
    }

    data class ConditionalPocket(val condition: Equation, val pocket: VirtualPocket) {
        companion object {
            val CODEC: Codec<ConditionalPocket> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Equation.CODEC.fieldOf("condition").forGetter(ConditionalPocket::condition),
                    VirtualPocket.CODEC.fieldOf("pocket").forGetter(ConditionalPocket::pocket)
                ).apply(instance, ::ConditionalPocket)
            }
        }
    }

    companion object {
        val CODEC: MapCodec<ConditionalSelector> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                ConditionalPocket.CODEC.mutableList().fieldOf("pockets").forGetter(ConditionalSelector::pocketMap)
            ).apply(instance, ::ConditionalSelector)
        }

        const val KEY: String = "conditional"
    }
}
