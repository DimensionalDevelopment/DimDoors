package org.dimdev.dimdoors.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.pockets.DefaultDungeonDestinations;
import org.dimdev.dimdoors.world.structure.processors.DestinationDataModifier;

import java.util.List;

public class ModProcessorLists {
    public static ResourceKey<StructureProcessorList> DUNGEON = ResourceKey.create(Registries.PROCESSOR_LIST, DimensionalDoors.id("dungeon"));

    public static void bootstrap(BootstapContext<StructureProcessorList> context) {
        context.register(DUNGEON, new StructureProcessorList(List.of(DestinationDataModifier.of(DefaultDungeonDestinations.getShallowerDungeonDestination()))));
    }
}
