package org.dimdev.dimdoors.world;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.tag.ModBiomeTags;

import java.util.Collections;

public class ModStructures {
    public static ResourceKey<Structure> TWO_PILLARS = key("two_pillars");
    public static ResourceKey<Structure> ICE_PILLARS = key("i_pillars");
    public static ResourceKey<Structure> SANDSTONE_PILLARS = key("sandstone_pillars");
    public static ResourceKey<Structure> RED_SANDSTONE_PILLARS = key("red_sandstone_pillars");

    public static ResourceKey<Structure> ENCLOSED_GATEWAY = key("enclosed_gateway");
    public static ResourceKey<Structure> ENCLOSED_ENDSTONE_GATEWAY = key("enclosed_endstone_gateway");
    public static ResourceKey<Structure> ENCLOSED_MUD_GATEWAY = key("enclosed_mud_gateway");
    public static ResourceKey<Structure> ENCLOSED_PRISMARINE_GATEWAY = key("enclosed_prismarine_gateway");
    public static ResourceKey<Structure> ENCLOSED_QUARTZ_GATEWAY = key("enclosed_quartz_gateway");
    public static ResourceKey<Structure> ENCLOSED_RED_SANDSTONE_GATEWAY = key("enclosed_red_sandstone_gateway");
    public static ResourceKey<Structure> ENCLOSED_SANDSTONE_GATEWAY = key("enclosed_sandstone_gateway");
//    public static ResourceKey<Structure> LIMBO_GATEWAY = key("limbo_gateway");

    private final HolderGetter<Biome> biomes;
    private final HolderGetter<StructureTemplatePool> pools;
    private final BootstrapContext<Structure> context;

    public ModStructures(BootstrapContext<Structure> context) {
        biomes = context.lookup(Registries.BIOME);
        pools = context.lookup(Registries.TEMPLATE_POOL);
        this.context = context;

        register(ENCLOSED_GATEWAY, ModBiomeTags.ENCLOSED_GATEWAY, ModGatewayPools.ENCLOSED_GATEWAY);
        register(ENCLOSED_ENDSTONE_GATEWAY, ModBiomeTags.ENCLOSED_ENDSTONE_GATEWAY, ModGatewayPools.ENCLOSED_ENDSTONE_GATEWAY);
        register(ENCLOSED_MUD_GATEWAY, ModBiomeTags.ENCLOSED_MUD_GATEWAY, ModGatewayPools.ENCLOSED_MUD_GATEWAY);
        context.register(ENCLOSED_PRISMARINE_GATEWAY, new JigsawStructure(new Structure.StructureSettings(biomes.getOrThrow(ModBiomeTags.ENCLOSED_PRISMARINE_GATEWAY), Collections.emptyMap(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN), pools.getOrThrow(ModGatewayPools.ENCLOSED_PRISMARINE_GATEWAY), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.OCEAN_FLOOR_WG));
        register(ENCLOSED_QUARTZ_GATEWAY, ModBiomeTags.ENCLOSED_QUARTZ_GATEWAY, ModGatewayPools.ENCLOSED_QUARTZ_GATEWAY);
        register(ENCLOSED_RED_SANDSTONE_GATEWAY, ModBiomeTags.ENCLOSED_RED_SANDSTONE_GATEWAY, ModGatewayPools.ENCLOSED_RED_SANDSTONE_GATEWAY);
        register(ENCLOSED_SANDSTONE_GATEWAY, ModBiomeTags.ENCLOSED_SANDSTONE_GATEWAY, ModGatewayPools.ENCLOSED_SANDSTONE_GATEWAY);
//        register(LIMBO_GATEWAY, ModBiomeTags.LIMBO_GATEWAY, ModGatewayPools.LIMBO_GATEWAY);

//        register(TWO_PILLARS, ModBiomeTags.TWO_PILLARS, ModGatewayPools.TWO_PILLARS);
//        register(SANDSTONE_PILLARS, ModBiomeTags.SANDSTONE_PILLARS, ModGatewayPools.SANDSTONE_PILLARS);
//        register(RED_SANDSTONE_PILLARS, ModBiomeTags.RED_SANDSTONE_PILLARS, ModGatewayPools.RED_SANDSTONE_PILLARS);
//        register(ICE_PILLARS, ModBiomeTags.ICE_PILLARS, ModGatewayPools.ICE_PILLARS);
    }

    private void register(ResourceKey<Structure> structure, TagKey<Biome> biome, ResourceKey<StructureTemplatePool> pool) {
        context.register(structure, new JigsawStructure(new Structure.StructureSettings(biomes.getOrThrow(biome), Collections.emptyMap(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN), pools.getOrThrow(pool), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
    }

    private static ResourceKey<Structure> key(String name) {
        return ResourceKey.create(Registries.STRUCTURE, DimensionalDoors.id(name));
    }
}

