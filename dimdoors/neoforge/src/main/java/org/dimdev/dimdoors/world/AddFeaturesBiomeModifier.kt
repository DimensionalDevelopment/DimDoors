package org.dimdev.dimdoors.world

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import net.neoforged.neoforge.common.world.BiomeModifier
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo

data class AddFeaturesBiomeModifier(
    val whiteList: HolderSet<Biome>,
    val blackList: HolderSet<Biome>,
    val features: HolderSet<PlacedFeature>,
    val step: GenerationStep.Decoration
) : BiomeModifier {
    override fun modify(
        biome: Holder<Biome>,
        phase: BiomeModifier.Phase,
        builder: ModifiableBiomeInfo.BiomeInfo.Builder
    ) {
        if (phase == BiomeModifier.Phase.ADD && this.whiteList.contains(biome) && (!blackList.contains(biome))) {
            val generationSettings = builder.generationSettings
            this.features.forEach { holder -> generationSettings.addFeature(this.step, holder) }
        }
    }

    override fun codec(): MapCodec<AddFeaturesBiomeModifier> = CODEC

    companion object {
        val CODEC: MapCodec<AddFeaturesBiomeModifier> = RecordCodecBuilder.mapCodec { builder -> builder.group(
            Biome.LIST_CODEC.fieldOf("whiteList").forGetter(AddFeaturesBiomeModifier::whiteList),
            Biome.LIST_CODEC.fieldOf("blackList").forGetter(AddFeaturesBiomeModifier::blackList),
            PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(AddFeaturesBiomeModifier::features),
            GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(AddFeaturesBiomeModifier::step)
            ).apply(builder, ::AddFeaturesBiomeModifier)
        }
    }
}