package org.dimdev.dimdoors.item.door.data.condition

import com.mojang.serialization.MapCodec
import net.minecraft.core.Holder
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors

import org.dimdev.dimdoors.ModRegistryKeys

object Conditions : PlatformRegistry.MapCodecPlatformRegistry<Condition>(ModRegistryKeys.CONDITION_TYPE, DimensionalDoors.getSided()) {
    val ALWAYS_TRUE = create("always_true") { AlwaysTrueCondition.codec }
    val ALL = create("all") { AllCondition.CODEC }
    val ANY = create("any") { AnyCondition.CODEC }
    val INVERSE = create("inverse") { InverseCondition.CODEC }
    val WORLD_MATCH = create("world_match") { WorldMatchCondition.CODEC }
    val BIOME = create("biome") { BiomeCondition.CODEC }
    val HEIGHT = create("height") { HeightCondition.CODEC }
    val WATERLOGGED = create("waterlogged") { WaterloggedCondition.CODEC }
    val LIGHT_LEVEL = create("light_level") { LightLevelCondition.CODEC }
}