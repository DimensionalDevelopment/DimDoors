package org.dimdev.dimdoors.world;

import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.mixin.BuiltinBiomesAccessor;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.util.BlankBiomeBuilder;

import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public final class ModBiomes {
    public static final RegistryKey<Biome> PERSONAL_WHITE_VOID_KEY;
    public static final RegistryKey<Biome> PUBLIC_BLACK_VOID_KEY;
    public static final RegistryKey<Biome> DUNGEON_DANGEROUS_BLACK_VOID_KEY;
    public static final RegistryKey<Biome> LIMBO_KEY;
    public static final Biome WHITE_VOID_BIOME;
    public static final Biome BLACK_VOID_BIOME;
    public static final Biome DANGEROUS_BLACK_VOID_BIOME;
    public static final Biome LIMBO_BIOME;

    public static void init() {
        int id = 1;
        id += BuiltinRegistries.BIOME.stream().count();
        BuiltinBiomesAccessor.invokeRegister(id + 1, LIMBO_KEY, LIMBO_BIOME);
        BuiltinBiomesAccessor.invokeRegister(id + 2, PERSONAL_WHITE_VOID_KEY, WHITE_VOID_BIOME);
        BuiltinBiomesAccessor.invokeRegister(id + 3, PUBLIC_BLACK_VOID_KEY, BLACK_VOID_BIOME);
        BuiltinBiomesAccessor.invokeRegister(id + 4, DUNGEON_DANGEROUS_BLACK_VOID_KEY, DANGEROUS_BLACK_VOID_BIOME);
    }

    static {
        PERSONAL_WHITE_VOID_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("dimdoors:white_void"));
        PUBLIC_BLACK_VOID_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("dimdoors:black_void"));
        DUNGEON_DANGEROUS_BLACK_VOID_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("dimdoors:dangerous_black_void"));
        LIMBO_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("dimdoors", "limbo"));
        WHITE_VOID_BIOME = new BlankBiomeBuilder(true, false).build();
        BLACK_VOID_BIOME = new BlankBiomeBuilder(false, false).build();
        DANGEROUS_BLACK_VOID_BIOME = new BlankBiomeBuilder(false, true).build();
        LIMBO_BIOME = new Biome.Builder()
                .category(Biome.Category.NONE)
                .depth(0.1f)
                .downfall(0.0f)
                .effects(new BiomeEffects.Builder()
                        .fogColor(0)
                        .waterColor(0)
                        .foliageColor(0)
                        .waterFogColor(0)
                        .moodSound(new BiomeMoodSound(ModSoundEvents.CREEPY, 6000, 8, 2.0))
                        .skyColor(0x404040)
                        .build())
                .generationSettings(new GenerationSettings.Builder()
                        .surfaceBuilder(SurfaceBuilder.NETHER.method_30478(new TernarySurfaceConfig(ModBlocks.UNRAVELLED_FABRIC.getDefaultState(), ModBlocks.UNRAVELLED_FABRIC.getDefaultState(), ModBlocks.ETERNAL_FLUID.getDefaultState())))
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
    }
}
