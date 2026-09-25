package org.dimdev.dimdoors.world

import com.mojang.serialization.MapCodec
import net.neoforged.neoforge.common.world.BiomeModifier
import net.neoforged.neoforge.registries.NeoForgeRegistries
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors

object ModBiomeModifiers : PlatformRegistry<MapCodec<out BiomeModifier>>(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, NeoForgeRegistries.BIOME_MODIFIER_SERIALIZERS, DimensionalDoors.getSided()) {

    @JvmField
    val ADD_FEATURES_BIOME_MODIFIER_TYPE = create("add_features") { AddFeaturesBiomeModifier.CODEC }
}
