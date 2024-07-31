package org.dimdev.dimdoors.world;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.dimdev.dimdoors.DimensionalDoors;

import java.util.Collections;

public class ModStructures {
    public static ResourceKey<Structure> TWO_PILLARS = ResourceKey.create(Registries.STRUCTURE, DimensionalDoors.id("two_pillars"));

    public static Registrar<StructureType<?>> STRUCTURE_TYPES = RegistrarManager.get(DimensionalDoors.MOD_ID).get(Registries.STRUCTURE_TYPE);

    public static void init() {

    }

    public static void bootstrap(BootstapContext<Structure> context) {
        var biomes = context.lookup(Registries.BIOME);
        var pools = context.lookup(Registries.TEMPLATE_POOL);
        context.register(TWO_PILLARS, new JigsawStructure(new Structure.StructureSettings(
                biomes.getOrThrow(BiomeTags.HAS_VILLAGE_DESERT),
                Collections.emptyMap(),
                GenerationStep.Decoration.SURFACE_STRUCTURES,
                TerrainAdjustment.BEARD_THIN),
                pools.getOrThrow(ModGatewayPools.TWO_PILLARS), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
    }
}

