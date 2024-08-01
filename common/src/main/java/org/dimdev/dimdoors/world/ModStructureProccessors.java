package org.dimdev.dimdoors.world;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.structure.processors.DestinationDataModifier;

public class ModStructureProccessors {
    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSORS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.STRUCTURE_PROCESSOR);
//    public static final RegistrySupplier<StructureProcessorType<?>> RANDOM_BITS = STRUCTURE_PROCESSORS.register("random_bits", () -> new StructureProcessorType<StructureProcessor>() {
//    });
    public static final RegistrySupplier<StructureProcessorType<DestinationDataModifier>> DESTINATION_DATA = STRUCTURE_PROCESSORS.register("destination_data", () -> () -> DestinationDataModifier.CODEC);

    private static ResourceKey<StructureProcessorType<?>> key(String name) {
        return ResourceKey.create(Registries.STRUCTURE_PROCESSOR, DimensionalDoors.id(name));
    }

    public static void init() {
        STRUCTURE_PROCESSORS.register();
    }
}
