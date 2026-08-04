package org.dimdev.dimdoors.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class TagUtils {
    public static boolean isIn(Level level, TagKey<DimensionType> tag) {
        return level.dimensionTypeRegistration().is(tag);
    }

    public static boolean isIn(Level level, ResourceKey<Level> key, TagKey<Level> tag) {
        return level.registryAccess()
                .registryOrThrow(key.registryKey())
                .getHolder(level.dimension())
                .map(holder -> holder.is(tag))
                .orElse(false);
    }
}
