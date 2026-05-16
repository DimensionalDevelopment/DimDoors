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
}
