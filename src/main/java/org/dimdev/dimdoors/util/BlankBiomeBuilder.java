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
        this.generationSettings(new GenerationSettings.Builder().surfaceBuilder(SurfaceBuilder.DEFAULT.method_30478(new TernarySurfaceConfig(Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), Blocks.VOID_AIR.getDefaultState()))).build())
                .precipitation(Biome.Precipitation.NONE)
                .category(Biome.Category.NONE)
                .depth(0)
                .temperatureModifier(Biome.TemperatureModifier.NONE)
                .downfall(0)
                .effects(this.createEffect(this.white))
                .scale(0)
                .temperature(0.8f)
                .temperatureModifier(null)
                .spawnSettings(new SpawnSettings.Builder().build());
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
}
