package org.dimdev.dimdoors.forge.world.feature.forge;

import dev.architectury.registry.level.biome.BiomeModifications;
import net.minecraftforge.common.Tags;

public class ModFeaturesPlacedImpl {
    public static boolean isDesert(BiomeModifications.BiomeContext context) {
        return context.hasTag(Tags.Biomes.IS_DESERT);
    }

    public static boolean isOcean(BiomeModifications.BiomeContext context) {
        return context.hasTag(Tags.Biomes.IS_WATER);
    }

    public static boolean isEnd(BiomeModifications.BiomeContext context) {
        return context.hasTag(Tags.Biomes.IS_HOT_END) ||
        context.hasTag(Tags.Biomes.IS_COLD_END) ||
        context.hasTag(Tags.Biomes.IS_SPARSE_END) ||
        context.hasTag(Tags.Biomes.IS_DENSE_END) ||
        context.hasTag(Tags.Biomes.IS_WET_END) ||
        context.hasTag(Tags.Biomes.IS_DRY_END);
    }

    public static boolean isOverworld(BiomeModifications.BiomeContext context) {
        return context.hasTag(Tags.Biomes.IS_HOT_OVERWORLD) ||
                context.hasTag(Tags.Biomes.IS_COLD_OVERWORLD) ||
                context.hasTag(Tags.Biomes.IS_SPARSE_OVERWORLD) ||
                context.hasTag(Tags.Biomes.IS_DENSE_OVERWORLD) ||
                context.hasTag(Tags.Biomes.IS_WET_OVERWORLD) ||
                context.hasTag(Tags.Biomes.IS_DRY_OVERWORLD);
    }
}
