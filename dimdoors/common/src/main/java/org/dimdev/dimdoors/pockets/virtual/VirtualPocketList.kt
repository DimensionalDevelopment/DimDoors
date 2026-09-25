package org.dimdev.dimdoors.pockets.virtual

import com.mojang.serialization.Codec
import org.dimdev.dimdoors.api.util.WeightedList
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference
import org.dimdev.dimdoors.world.pocket.type.Pocket

class VirtualPocketList(list: List<VirtualPocket> = emptyList()) : WeightedList<VirtualPocket, PocketGenerationContext>(list), VirtualPocket {
    override fun prepareAndPlacePocket(parameters: PocketGenerationContext): Pocket<*, *>? =
        getNextPocketGeneratorReference(parameters).prepareAndPlacePocket(parameters)

    override fun prepareAndPlacePocket(parameters: PocketGenerationContext, setupLoot: Boolean?): Pocket<*, *>? =
        getNextPocketGeneratorReference(parameters).prepareAndPlacePocket(parameters, setupLoot)

    override fun getNextPocketGeneratorReference(parameters: PocketGenerationContext): PocketGeneratorReference<*> =
        getNextRandomWeighted(parameters)!!.getNextPocketGeneratorReference(parameters)

    override fun peekNextPocketGeneratorReference(parameters: PocketGenerationContext): PocketGeneratorReference<*> =
        peekNextRandomWeighted(parameters)!!.peekNextPocketGeneratorReference(parameters)

    override fun getWeight(parameters: PocketGenerationContext): Double = getTotalWeight(parameters)

    companion object {
        @JvmField
        val CODEC: Codec<VirtualPocketList> = Codec.lazyInitialized {
            VirtualPocket.CODEC.listOf().xmap(::VirtualPocketList) { it }
        }
    }
}
