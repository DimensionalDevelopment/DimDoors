package org.dimdev.dimdoors.datagen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.ModStructures;

import java.util.List;

public class ModStructureSets {
    public static ResourceKey<StructureSet> GATEWAYS = ResourceKey.create(Registries.STRUCTURE_SET, DimensionalDoors.id("gateways"));
    public static void bootstrap(BootstapContext<StructureSet> context) {
        context.register(GATEWAYS, new StructureSet(
                List.of(new StructureSet.StructureSelectionEntry(context.lookup(Registries.STRUCTURE).getOrThrow(ModStructures.TWO_PILLARS), 1),
                        new StructureSet.StructureSelectionEntry(context.lookup(Registries.STRUCTURE).getOrThrow(ModStructures.ICE_PILLARS), 1),
                        new StructureSet.StructureSelectionEntry(context.lookup(Registries.STRUCTURE).getOrThrow(ModStructures.RED_SANDSTONE_PILLARS), 1),
                        new StructureSet.StructureSelectionEntry(context.lookup(Registries.STRUCTURE).getOrThrow(ModStructures.SANDSTONE_PILLARS), 1)), new RandomSpreadStructurePlacement(
                34,
                8,
                RandomSpreadType.LINEAR,
                23165478)));
    }
}
