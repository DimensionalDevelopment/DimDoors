package org.dimdev.dimdoors.world

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided
import org.dimdev.dimdoors.world.structure.processors.DestinationDataModifier

object ModStructureProccessors : PlatformRegistry.StructureProcessorPlatformRegistry(getSided()) {
    //    public static final RegistrySupplier<StructureProcessorType<?>> RANDOM_BITS = STRUCTURE_PROCESSORS.register("random_bits", () -> new StructureProcessorType<StructureProcessor>() {
    //    });
    val DESTINATION_DATA = create("destination_data", DestinationDataModifier.CODEC)

    fun init() {
    }
}
