package org.dimdev.dimdoors.world;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.structure.processors.DestinationDataModifier;

public class ModStructureProccessors {
    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSORS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registry.STRUCTURE_PROCESSOR_REGISTRY);
    public static final RegistrySupplier<StructureProcessorType<DestinationDataModifier>> DESTINATION_DATA = STRUCTURE_PROCESSORS.register("destination_data", () -> () -> DestinationDataModifier.CODEC);

    public static void init() {
        STRUCTURE_PROCESSORS.register();
    }
}
