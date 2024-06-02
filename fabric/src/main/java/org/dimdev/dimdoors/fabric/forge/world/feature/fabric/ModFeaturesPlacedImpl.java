package org.dimdev.dimdoors.fabric.forge.world.feature.fabric;

import dev.architectury.registry.level.biome.BiomeModifications;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;

public class ModFeaturesPlacedImpl {
    public static boolean isDesert(BiomeModifications.BiomeContext context) {
        return context.hasTag(ConventionalBiomeTags.DESERT);
    }

    public static boolean isOcean(BiomeModifications.BiomeContext context) {
        return context.hasTag(ConventionalBiomeTags.AQUATIC) || context.hasTag(ConventionalBiomeTags.AQUATIC_ICY);
    }

    public static boolean isEnd(BiomeModifications.BiomeContext context) {
        return context.hasTag(ConventionalBiomeTags.IN_THE_END);
    }

    public static boolean isOverworld(BiomeModifications.BiomeContext context) {
        return context.hasTag(ConventionalBiomeTags.IN_OVERWORLD);
    }
}
