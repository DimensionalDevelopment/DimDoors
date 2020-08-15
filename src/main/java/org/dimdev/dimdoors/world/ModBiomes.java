package org.dimdev.dimdoors.world;

import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.mixin.BuiltinBiomesAccessor;
import org.dimdev.dimdoors.sound.ModSoundEvents;

import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.DefaultSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public final class ModBiomes {
    public static final Biome LIMBO_BIOME = new Biome.Builder()
            .category(Biome.Category.NONE)
            .depth(0.1f)
            .downfall(0.0f)
            .effects(new BiomeEffects.Builder()
                    .fogColor(0)
                    .waterColor(0)
                    .foliageColor(0)
                    .waterFogColor(0)
                    .moodSound(new BiomeMoodSound(ModSoundEvents.CREEPY, 6000, 8, 2.0))
                    .build())
            .generationSettings(new GenerationSettings.Builder()
                    .surfaceBuilder(new ConfiguredSurfaceBuilder<>(new DefaultSurfaceBuilder(TernarySurfaceConfig.CODEC), new TernarySurfaceConfig(ModBlocks.UNRAVELLED_FABRIC.getDefaultState(), ModBlocks.UNRAVELLED_FABRIC.getDefaultState(), ModBlocks.ETERNAL_FLUID.getDefaultState())))
                    .build())
            .precipitation(Biome.Precipitation.NONE)
            .scale(0.9f)
            .spawnSettings(new SpawnSettings.Builder()
                    .creatureSpawnProbability(0.2f)
                    .spawnCost(ModEntityTypes.MONOLITH, 5, 5)
                    .build())
            .temperature(0.2f)
            .temperatureModifier(Biome.TemperatureModifier.NONE)
            .build();
    public static final RegistryKey<Biome> LIMBO_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("dimdoors", "limbo"));
//    public static final RegistryKey<Biome> PERSONAL = RegistryKey.of(Registry.BIOME_KEY, new Identifier("dimdoors:white_void"));
//    public static final RegistryKey<Biome> PUBLIC = RegistryKey.of(Registry.BIOME_KEY, new Identifier("dimdoors:black_void"));
//    public static final RegistryKey<Biome> DUNGEON = RegistryKey.of(Registry.BIOME_KEY, new Identifier("dimdoors:dangerous_black_void"));

    public static void init() {
        BuiltinBiomesAccessor.invokeRegister(174, LIMBO_KEY, LIMBO_BIOME);
    }
}
