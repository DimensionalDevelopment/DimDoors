package org.dimdev.dimdoors.world.decay.pattern

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys

object DecayPatterns : PlatformRegistry.MapCodecPlatformRegistry<DecayPattern>(ModRegistryKeys.DECAY_PATTERN_TYPE, DimensionalDoors.getSided()) {
    val COMPOUND = create(CompoundDecayPattern.KEY) { CompoundDecayPattern.CODEC }
    val PAINTING = create(PaintingDecayPattern.KEY) { PaintingDecayPattern.CODEC }
}
