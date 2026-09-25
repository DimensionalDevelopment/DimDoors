package org.dimdev.dimdoors.util

import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.dimension.DimensionType
import kotlin.jvm.optionals.getOrNull

object TagUtils {
    fun isIn(level: Level, tag: TagKey<DimensionType>): Boolean {
        return level.dimensionTypeRegistration().`is`(tag)
    }

    fun isIn(level: Level, key: ResourceKey<Level>, tag: TagKey<Level>): Boolean {
        return level.registryAccess()
            .registryOrThrow(key.registryKey())
            .getHolder(level.dimension())
            .map { holder -> holder.`is`(tag) }
            .getOrNull() ?: false
    }
}
