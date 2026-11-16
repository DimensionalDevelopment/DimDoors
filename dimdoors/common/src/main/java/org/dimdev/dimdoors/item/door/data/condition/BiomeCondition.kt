package org.dimdev.dimdoors.item.door.data.condition

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.biome.Biome
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity

data class BiomeCondition(val biome: ResourceKey<Biome>) : Condition {
    override fun matches(rift: EntranceRiftBlockEntity<*>): Boolean = rift.level?.getBiome(rift.blockPos)?.`is`(biome) == true

    override val type: MapCodec<BiomeCondition> get() = Conditions.BIOME

    companion object {
        val CODEC: MapCodec<BiomeCondition> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(ResourceKey.codec(Registries.BIOME).fieldOf("biome").forGetter(BiomeCondition::biome)
            ).apply(instance, ::BiomeCondition)
        }
    }
}
