package org.dimdev.dimdoors.item.door.data.condition

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.biome.Biome
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity

data class BiomeCondition(val biome: ResourceKey<Biome>) : Condition {
    override fun matches(rift: EntranceRiftBlockEntity<*>): Boolean = rift.riftLevel.getBiome(rift.blockPos).`is`(biome)

    override val type get() = Conditions.BIOME

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec { instance ->
            instance.group(ResourceKey.codec(Registries.BIOME).fieldOf("biome").forGetter(BiomeCondition::biome)
            ).apply(instance, ::BiomeCondition)
        }
    }
}
