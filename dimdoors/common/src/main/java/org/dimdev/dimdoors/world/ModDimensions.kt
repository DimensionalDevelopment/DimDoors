package org.dimdev.dimdoors.world

import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
import net.minecraft.util.valueproviders.UniformInt
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.FixedBiomeSource
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.dimension.LevelStem
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.util.key
import org.dimdev.dimdoors.world.pocket.BlankChunkGenerator
import java.util.*

object ModDimensions {
    @JvmField val LIMBO = Registries.DIMENSION.key("limbo")
    @JvmField val PERSONAL = Registries.DIMENSION.key("personal_pockets")
    @JvmField val PUBLIC = Registries.DIMENSION.key("public_pockets")
    @JvmField val DUNGEON = Registries.DIMENSION.key("dungeon_pockets")

    val LIMBO_STEM = Registries.LEVEL_STEM.key("limbo")
    val PERSONAL_STEM = Registries.LEVEL_STEM.key("person")
    val PUBLIC_STEM = Registries.LEVEL_STEM.key("public")
    val DUNGEON_STEM = Registries.LEVEL_STEM.key("dungeon")

    @JvmField val LIMBO_TYPE_KEY = Registries.DIMENSION_TYPE.key("limbo")
    @JvmField val POCKET_TYPE_KEY = Registries.DIMENSION_TYPE.key("pocket")
    @JvmField val LIMBO_NOISE_SETTINGS = Registries.NOISE_SETTINGS.key("limbo")

    lateinit var LIMBO_DIMENSION: ServerLevel

    @JvmStatic
    fun isPocketDimension(world: Level?): Boolean {
        return world != null && isPocketDimension(world.dimension())
    }

    fun isPrivatePocketDimension(world: Level?): Boolean {
        return world != null && world.dimension() == PERSONAL
    }

    @JvmStatic
    fun isPocketDimension(type: ResourceKey<Level>): Boolean {
        return type == PERSONAL || type == PUBLIC || type == DUNGEON
    }


    fun isDungeonDimension(type: ResourceKey<Level?>?): Boolean {
        return type == PERSONAL || type == PUBLIC || type == DUNGEON
    }

    @JvmStatic
    fun isLimboDimension(world: Level): Boolean {
        return world.dimension() == LIMBO
    }

    fun register() {
        platform.onServerStarted { server ->
            LIMBO_DIMENSION = server.getLevel(LIMBO)!!
        }
    }

    fun bootstrap(entries: BootstrapContext<DimensionType>) {
        entries.register(
            LIMBO_TYPE_KEY,
            DimensionType(
                OptionalLong.of(6000),
                true,
                false,
                false,
                false,
                4.0,
                false,
                true,
                0,
                256,
                256,
                BlockTags.INFINIBURN_OVERWORLD,
                DimensionalDoors.id("limbo"),
                0.1f,
                DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)
            )
        )
        entries.register(
            POCKET_TYPE_KEY,
            DimensionType(
                OptionalLong.of(6000),
                true,
                false,
                false,
                false,
                4.0,
                false,
                true,
                0,
                256,
                256,
                BlockTags.INFINIBURN_OVERWORLD,
                DimensionalDoors.id("dungeon"),
                0.1f,
                DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)
            )
        )
    }

    private fun createPocketStem(dimensionType: Holder<DimensionType>, biome: Holder<Biome>): LevelStem = LevelStem(dimensionType, BlankChunkGenerator.of(FixedBiomeSource(biome)))
}
