package org.dimdev.dimdoors.datagen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.ModStructures;

public class ModStructureSets {
    public static ResourceKey<StructureSet> GATEWAYS = ResourceKey.create(Registries.STRUCTURE_SET, DimensionalDoors.id("gateways"));
    public static void bootstrap(BootstapContext<StructureSet> context) {
        context.register(GATEWAYS, new StructureSet(context.lookup(Registries.STRUCTURE).getOrThrow(ModStructures.TWO_PILLARS), new RandomSpreadStructurePlacement(
                34,
                8,
                RandomSpreadType.LINEAR,
                23165478)));
    }
}
