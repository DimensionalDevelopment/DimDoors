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

    private static ResourceKey<Structure> key(String name) {
        return ResourceKey.create(Registries.STRUCTURE, DimensionalDoors.id(name));
    }
}

