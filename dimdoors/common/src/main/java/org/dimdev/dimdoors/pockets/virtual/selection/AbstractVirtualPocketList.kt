package org.dimdev.dimdoors.pockets.virtual.selection

import org.dimdev.dimdoors.api.util.WeightedList
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference
import org.dimdev.dimdoors.world.pocket.type.Pocket

abstract class AbstractVirtualPocketList<T : AbstractVirtualPocketList<T>> : WeightedList<VirtualPocket, PocketGenerationContext>(), ImplementedVirtualPocket<T> {
    override fun prepareAndPlacePocket(parameters: PocketGenerationContext): Pocket<*, *>? =
        getNextPocketGeneratorReference(parameters).prepareAndPlacePocket(parameters)

    override fun prepareAndPlacePocket(parameters: PocketGenerationContext, setupLoot: Boolean?): Pocket<*, *>? =
        getNextPocketGeneratorReference(parameters).prepareAndPlacePocket(parameters, setupLoot)

    override fun getNextPocketGeneratorReference(parameters: PocketGenerationContext): PocketGeneratorReference<*> =
        getNextRandomWeighted(parameters)!!.getNextPocketGeneratorReference(parameters)

    override fun peekNextPocketGeneratorReference(parameters: PocketGenerationContext): PocketGeneratorReference<*> =
        peekNextRandomWeighted(parameters)!!.peekNextPocketGeneratorReference(parameters)

    override fun getWeight(parameters: PocketGenerationContext): Double = getTotalWeight(parameters)
}
