package org.dimdev.dimdoors.pockets.generator

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided
import org.dimdev.dimdoors.ModRegistryKeys


object PocketGenerators : PlatformRegistry.MapCodecPlatformRegistry<PocketGenerator<*>>(ModRegistryKeys.POCKET_GENERATOR_TYPE, getSided()) {
    @JvmField val SCHEMATIC = create(SchematicGenerator.KEY) { SchematicGenerator.CODEC }
    @JvmField val VOID = create(VoidGenerator.KEY) { VoidGenerator.CODEC }
}
