package org.dimdev.dimdoors.datagen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.ModStructures;

import java.util.List;

public class ModStructureSets {
    public static ResourceKey<StructureSet> GATEWAYS = ResourceKey.create(Registries.STRUCTURE_SET, DimensionalDoors.id("gateways"));
    public static void bootstrap(BootstrapContext<StructureSet> context) {
        context.register(GATEWAYS, new StructureSet(

                List.of(new StructureSet.StructureSelectionEntry(context.lookup(Registries.STRUCTURE).getOrThrow(ModStructures.ENCLOSED_GATEWAY), 1),
                        new StructureSet.StructureSelectionEntry(context.lookup(Registries.STRUCTURE).getOrThrow(ModStructures.ENCLOSED_ENDSTONE_GATEWAY), 1),
                        new StructureSet.StructureSelectionEntry(context.lookup(Registries.STRUCTURE).getOrThrow(ModStructures.ENCLOSED_MUD_GATEWAY), 1),
                        new StructureSet.StructureSelectionEntry(context.lookup(Registries.STRUCTURE).getOrThrow(ModStructures.ENCLOSED_PRISMARINE_GATEWAY), 1),
                        new StructureSet.StructureSelectionEntry(context.lookup(Registries.STRUCTURE).getOrThrow(ModStructures.ENCLOSED_QUARTZ_GATEWAY), 1),
                        new StructureSet.StructureSelectionEntry(context.lookup(Registries.STRUCTURE).getOrThrow(ModStructures.ENCLOSED_RED_SANDSTONE_GATEWAY), 1),
                        new StructureSet.StructureSelectionEntry(context.lookup(Registries.STRUCTURE).getOrThrow(ModStructures.ENCLOSED_SANDSTONE_GATEWAY), 1)/*,
                        new StructureSet.StructureSelectionEntry(context.lookup(Registries.STRUCTURE).getOrThrow(ModStructures.LIMBO_GATEWAY), 1)*/),
                new RandomSpreadStructurePlacement(
                15,
                5,
                RandomSpreadType.TRIANGULAR,
                23165478)));
    }
}
