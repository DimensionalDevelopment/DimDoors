package org.dimdev.dimdoors.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.structure.processors.DestinationDataModifier;

public class ModStructureProccessors {
//    public static final RegistrySupplier<StructureProcessorType<?>> RANDOM_BITS = STRUCTURE_PROCESSORS.register("random_bits", () -> new StructureProcessorType<StructureProcessor>() {
//    });
    public static final StructureProcessorType<DestinationDataModifier> DESTINATION_DATA = DimensionalDoors.getSided().registerStructureProcessor("destination_data", DestinationDataModifier.CODEC);

    private static ResourceKey<StructureProcessorType<?>> key(String name) {
        return ResourceKey.create(Registries.STRUCTURE_PROCESSOR, DimensionalDoors.id(name));
    }

    public static void init() {
    }
}
