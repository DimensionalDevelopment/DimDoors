package org.dimdev.dimdoors.world;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModStructures {
    public static Registrar<StructureType<?>> STRUCTURE_TYPES = RegistrarManager.get(DimensionalDoors.MOD_ID).get(Registries.STRUCTURE_TYPE);

    public static void init() {
    }
}

