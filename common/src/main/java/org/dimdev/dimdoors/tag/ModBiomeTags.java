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

    private static TagKey<Biome> of(String id) {
        return TagKey.create(Registries.BIOME, DimensionalDoors.id(id));
    }

}
