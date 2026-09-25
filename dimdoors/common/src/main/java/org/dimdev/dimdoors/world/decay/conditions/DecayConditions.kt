package org.dimdev.dimdoors.world.decay.conditions

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys

object DecayConditions : PlatformRegistry.MapCodecPlatformRegistry<DecayCondition>(ModRegistryKeys.DECAY_CONDITION_TYPE, DimensionalDoors.getSided()) {

//    @JvmField
//    val CODEC: Codec<DecayConditionType<out DecayCondition?>?> = REGISTRY.byNameCodec()


    val NONE = create("none") { NoneDecayCondition.codec }
    val BLOCK = create("block") { BlockDecayCondition.CODEC }
    val FLUID = create("fluid") { FluidDecayCondition.CODEC }
    val DECAY_SOURCE = create("source") { DecaySourceCondition.CODEC }
    val DIMENSION = create("dimension") { DimensionDecayCondition.CODEC }
}
