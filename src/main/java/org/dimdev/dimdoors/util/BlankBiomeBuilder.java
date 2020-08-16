package org.dimdev.dimdoors.util;

import org.dimdev.dimdoors.sound.ModSoundEvents;

import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class BlankBiomeBuilder extends Biome.Builder {
    private final boolean white;
    private final boolean dangerous;

    public BlankBiomeBuilder(boolean white, boolean dangerous) {
        this.white = white;
        this.dangerous = dangerous;
        this.generationSettings(null).precipitation(null).category(null).depth(0).temperatureModifier(null).downfall(0).effects(null).scale(0).temperature(0).temperatureModifier(null);
    }

    private BiomeEffects createEffect(boolean white) {
        BiomeEffects.Builder builder = new BiomeEffects.Builder()
                .waterColor(white ? 0xFFFFFF : 0x000000)
                .waterFogColor(white ? 0xFFFFFF : 0x000000)
                .fogColor(white ? 0xFFFFFF : 0x000000)
                .skyColor(white ? 0xFFFFFF : 0x808080)
                .grassColorModifier(BiomeEffects.GrassColorModifier.NONE);
        if (white) builder.loopSound(ModSoundEvents.WHITE_VOID);
        return builder.build();
    }

    @Override
    public BlankBiomeBuilder generationSettings(GenerationSettings generationSettings) {
        return (BlankBiomeBuilder) super.generationSettings(new GenerationSettings.Builder().surfaceBuilder(SurfaceBuilder.DEFAULT.method_30478(new TernarySurfaceConfig(Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), Blocks.VOID_AIR.getDefaultState()))).build());
    }

    @Override
    public BlankBiomeBuilder precipitation(Biome.Precipitation precipitation) {
        return (BlankBiomeBuilder) super.precipitation(Biome.Precipitation.NONE);
    }

    @Override
    public BlankBiomeBuilder category(Biome.Category category) {
        return (BlankBiomeBuilder) super.category(Biome.Category.NONE);
    }

    @Override
    public BlankBiomeBuilder scale(float scale) {
        return (BlankBiomeBuilder) super.scale(0);
    }

    @Override
    public BlankBiomeBuilder depth(float depth) {
        return (BlankBiomeBuilder) super.depth(0);
    }

    @Override
    public BlankBiomeBuilder downfall(float downfall) {
        return (BlankBiomeBuilder) super.downfall(0);
    }

    @Override
    public BlankBiomeBuilder temperature(float temperature) {
        return (BlankBiomeBuilder) super.temperature(0.8f);
    }

    @Override
    public BlankBiomeBuilder effects(BiomeEffects effects) {
        return (BlankBiomeBuilder) super.effects(this.createEffect(this.white));
    }

    @Override
    public BlankBiomeBuilder temperatureModifier(Biome.TemperatureModifier temperatureModifier) {
        return (BlankBiomeBuilder) super.temperatureModifier(Biome.TemperatureModifier.NONE);
    }

    @Override
    public BlankBiomeBuilder spawnSettings(SpawnSettings spawnSettings) {
        return (BlankBiomeBuilder) super.spawnSettings(spawnSettings);
    }
}
