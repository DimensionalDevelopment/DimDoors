package org.dimdev.dimdoors.world;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeGenerationSettingsBuilder;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

import static org.dimdev.dimdoors.world.ModBiomeModifiers.ADD_FEATURES_BIOME_MODIFIER_TYPE;

public record AddFeaturesBiomeModifier(HolderSet<Biome> whiteList, HolderSet<Biome> blackList, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) implements BiomeModifier {

    public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {

        if (phase == Phase.ADD && this.whiteList.contains(biome) && (blackList == null || !blackList.contains(biome))) {

            BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
            this.features.forEach((holder) -> {
                generationSettings.addFeature(this.step, holder);
            });
        }

    }

    public MapCodec<AddFeaturesBiomeModifier> codec() {
        return ADD_FEATURES_BIOME_MODIFIER_TYPE.get();
    }


}