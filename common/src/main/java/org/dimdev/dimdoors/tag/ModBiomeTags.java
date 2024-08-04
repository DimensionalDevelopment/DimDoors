package org.dimdev.dimdoors.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModBiomeTags {

    public static final TagKey<Biome> TWO_PILLARS = of("two_pillars");
    public static final TagKey<Biome> ICE_PILLARS = of("ice_pillars");
    public static final TagKey<Biome> RED_SANDSTONE_PILLARS = of("red_sandstone_pillars");
    public static final TagKey<Biome> SANDSTONE_PILLARS = of("sandstone_pillars");
    public static final TagKey<Biome> ENCLOSED_GATEWAY = of("enclosed_gateway");
    public static final TagKey<Biome> ENCLOSED_ENDSTONE_GATEWAY = of("enclosed_endstone_gateway");
    public static final TagKey<Biome> ENCLOSED_MUD_GATEWAY = of("enclosed_mud_gateway");
    public static final TagKey<Biome> ENCLOSED_PRISMARINE_GATEWAY = of("enclosed_prismarine_gateway");
    public static final TagKey<Biome> ENCLOSED_QUARTZ_GATEWAY = of("enclosed_quartz_gateway");
    public static final TagKey<Biome> ENCLOSED_RED_SANDSTONE_GATEWAY = of("enclosed_red_sandstone_gateway");
    public static final TagKey<Biome> ENCLOSED_SANDSTONE_GATEWAY = of("enclosed_sandstone_gateway");
//    public static final TagKey<Biome> LIMBO_GATEWAY = of("limbo_gateway");

    private static TagKey<Biome> of(String id) {
        return TagKey.create(Registries.BIOME, DimensionalDoors.id(id));
    }

}
