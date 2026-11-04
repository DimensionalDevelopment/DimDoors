package org.dimdev.dimdoors.pockets.virtual

import com.mojang.serialization.MapCodec
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference
import org.dimdev.dimdoors.world.pocket.type.Pocket

// TODO: NoneReference instead?
class NoneVirtualPocket private constructor() : ImplementedVirtualPocket<NoneVirtualPocket> {
    override fun prepareAndPlacePocket(parameters: PocketGenerationContext): Pocket<*, *> {
        throw UnsupportedOperationException("Cannot place a NoneVirtualPocket")
    }

    override fun prepareAndPlacePocket(parameters: PocketGenerationContext, setupLoot: Boolean?): Pocket<*, *> {
        throw UnsupportedOperationException("Cannot place a NoneVirtualPocket")
    }

    override fun getNextPocketGeneratorReference(parameters: PocketGenerationContext): PocketGeneratorReference<*> {
        throw UnsupportedOperationException("Cannot get next pocket generator reference on a NoneVirtualPocket")
    }

    override fun peekNextPocketGeneratorReference(parameters: PocketGenerationContext): PocketGeneratorReference<*> {
        throw UnsupportedOperationException("Cannot peek next pocket generator reference on a NoneVirtualPocket")
    }

    override fun getWeight(parameters: PocketGenerationContext): Double {
        return 0.0
    }

    override val type: MapCodec<NoneVirtualPocket> get() = VirtualPockets.NONE

    companion object {
        const val KEY: String = "none"
        @JvmField
        val NONE: NoneVirtualPocket = NoneVirtualPocket()
    }
}