package org.dimdev.dimdoors.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

import static org.dimdev.dimdoors.world.ModBiomeModifiers.ADD_FEATURES_BIOME_MODIFIER_TYPE;

public record AddFeaturesBiomeModifier(HolderSet<Biome> whiteList, HolderSet<Biome> blackList, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) implements BiomeModifier {
    public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        System.out.println("RawrRawr: " + biome);

        if (phase == Phase.ADD && this.whiteList.contains(biome) && (blackList == null || !blackList.contains(biome))) {
            System.out.println("RAWR! ^_=_^");

            BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
            this.features.forEach((holder) -> {
                generationSettings.addFeature(this.step, holder);
            });
        }

    }

    public Codec<? extends BiomeModifier> codec() {
        return ADD_FEATURES_BIOME_MODIFIER_TYPE.get();
    }


}