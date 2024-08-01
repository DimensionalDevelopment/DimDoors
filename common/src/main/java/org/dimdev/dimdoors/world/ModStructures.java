package org.dimdev.dimdoors.world;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.tag.ModBiomeTags;

import java.util.Collections;

public class ModStructures {
    public static ResourceKey<Structure> TWO_PILLARS = ResourceKey.create(Registries.STRUCTURE, DimensionalDoors.id("two_pillars"));
    public static ResourceKey<Structure> ICE_PILLARS = ResourceKey.create(Registries.STRUCTURE, DimensionalDoors.id("i_pillars"));
    public static ResourceKey<Structure> SANDSTONE_PILLARS = ResourceKey.create(Registries.STRUCTURE, DimensionalDoors.id("sandstone_pillars"));
    public static ResourceKey<Structure> RED_SANDSTONE_PILLARS = ResourceKey.create(Registries.STRUCTURE, DimensionalDoors.id("red_sandstone_pillars"));


    public static Registrar<StructureType<?>> STRUCTURE_TYPES = RegistrarManager.get(DimensionalDoors.MOD_ID).get(Registries.STRUCTURE_TYPE);
    private final HolderGetter<Biome> biomes;
    private final HolderGetter<StructureTemplatePool> pools;
    private final BootstapContext<Structure> context;

    public ModStructures(BootstapContext<Structure> context) {
        biomes = context.lookup(Registries.BIOME);
        pools = context.lookup(Registries.TEMPLATE_POOL);
        this.context = context;

        register(TWO_PILLARS, ModBiomeTags.TWO_PILLARS, ModGatewayPools.TWO_PILLARS);
        register(SANDSTONE_PILLARS, ModBiomeTags.SANDSTONE_PILLARS, ModGatewayPools.SANDSTONE_PILLARS);
        register(RED_SANDSTONE_PILLARS, ModBiomeTags.RED_SANDSTONE_PILLARS, ModGatewayPools.RED_SANDSTONE_PILLARS);
        register(ICE_PILLARS, ModBiomeTags.ICE_PILLARS, ModGatewayPools.ICE_PILLARS);
    }

    private void register(ResourceKey<Structure> structure, TagKey<Biome> biome, ResourceKey<StructureTemplatePool> pool) {
        context.register(structure, new JigsawStructure(new Structure.StructureSettings(biomes.getOrThrow(biome), Collections.emptyMap(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN), pools.getOrThrow(pool), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
    }
}

