package org.dimdev.dimdoors.world.decay.results

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys

object DecayResults : PlatformRegistry.MapCodecPlatformRegistry<DecayResult>(ModRegistryKeys.DECAY_RESULT_TYPE, DimensionalDoors.getSided()) {
    val SINGLE_BLOCK = create(SingleBlockDecayResult.KEY) { SingleBlockDecayResult.CODEC }
    val NONE = create(NoneDecayResult.KEY) { NoneDecayResult.codec }
    val SELF = create(SelfDecayResult.KEY) { SelfDecayResult.codec }
    val DOUBLE_BLOCK = create(DoubleBlockDecayResult.KEY) { DoubleBlockDecayResult.CODEC }
    val FLUID = create(FluidDecayResult.KEY) { FluidDecayResult.CODEC }
}
