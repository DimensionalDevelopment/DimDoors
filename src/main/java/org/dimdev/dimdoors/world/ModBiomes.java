package org.dimdev.dimdoors.world;

import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.particle.ModParticleTypes;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.feature.ModFeatures;

import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;

public final class ModBiomes {
    public static final RegistryKey<Biome> PERSONAL_WHITE_VOID_KEY;
    public static final RegistryKey<Biome> PUBLIC_BLACK_VOID_KEY;
    public static final RegistryKey<Biome> DUNGEON_DANGEROUS_BLACK_VOID_KEY;
    public static final RegistryKey<Biome> LIMBO_KEY;
    public static final Biome PERSONAL_WHITE_VOID_BIOME;
    public static final Biome PUBLIC_BLACK_VOID_BIOME;
    public static final Biome DUNGEON_DANGEROUS_BLACK_VOID_BIOME;
    public static final Biome LIMBO_BIOME;

    public static void init() {
        Registry.register(BuiltinRegistries.BIOME, LIMBO_KEY.getValue(), LIMBO_BIOME);
        Registry.register(BuiltinRegistries.BIOME, PERSONAL_WHITE_VOID_KEY.getValue(), PERSONAL_WHITE_VOID_BIOME);
        Registry.register(BuiltinRegistries.BIOME, PUBLIC_BLACK_VOID_KEY.getValue(), PUBLIC_BLACK_VOID_BIOME);
        Registry.register(BuiltinRegistries.BIOME, DUNGEON_DANGEROUS_BLACK_VOID_KEY.getValue(), DUNGEON_DANGEROUS_BLACK_VOID_BIOME);
        // only ever needed if the biome api is broken
//        BuiltinBiomesAccessor.getIdMap().put(BuiltinRegistries.BIOME.getRawId(LIMBO_BIOME), LIMBO_KEY);
//        BuiltinBiomesAccessor.getIdMap().put(BuiltinRegistries.BIOME.getRawId(PERSONAL_WHITE_VOID_BIOME), PERSONAL_WHITE_VOID_KEY);
//        BuiltinBiomesAccessor.getIdMap().put(BuiltinRegistries.BIOME.getRawId(PUBLIC_BLACK_VOID_BIOME), PUBLIC_BLACK_VOID_KEY);
//        BuiltinBiomesAccessor.getIdMap().put(BuiltinRegistries.BIOME.getRawId(DUNGEON_DANGEROUS_BLACK_VOID_BIOME), DUNGEON_DANGEROUS_BLACK_VOID_KEY);
    }

    private static BiomeEffects createEffect(boolean white) {
        BiomeEffects.Builder builder = new BiomeEffects.Builder()
                .waterColor(0x3f76e4)
                .waterFogColor(0x50533)
                .fogColor(white ? 0xFFFFFF : 0x000000)
                .skyColor(white ? 0xFFFFFF : 0x000000)
                .grassColorModifier(BiomeEffects.GrassColorModifier.NONE);
        if (white) {
            builder.music(new MusicSound(ModSoundEvents.WHITE_VOID, 0, 0, true));
        }
        return builder.build();
    }

    static {
        Biome.Builder voidBiomeBuilder = new Biome.Builder()
				.category(Biome.Category.NONE)
				.downfall(0)
				.generationSettings(new GenerationSettings.Builder().build())
				.precipitation(Biome.Precipitation.NONE)
				.spawnSettings(new SpawnSettings.Builder().build())
				.temperature(0.8f)
				.temperatureModifier(Biome.TemperatureModifier.NONE);
        PERSONAL_WHITE_VOID_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("dimdoors:white_void"));
        PUBLIC_BLACK_VOID_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("dimdoors:black_void"));
        DUNGEON_DANGEROUS_BLACK_VOID_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("dimdoors:dangerous_black_void"));
        LIMBO_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("dimdoors", "limbo"));
        PERSONAL_WHITE_VOID_BIOME = voidBiomeBuilder.effects(createEffect(true)).build();
        PUBLIC_BLACK_VOID_BIOME = voidBiomeBuilder.effects(createEffect(false)).build();
        DUNGEON_DANGEROUS_BLACK_VOID_BIOME = voidBiomeBuilder.effects(createEffect(false)).build();
        LIMBO_BIOME = new Biome.Builder()
                .category(Biome.Category.NONE)
                .downfall(0.0f)
                .effects(new BiomeEffects.Builder()
                        .fogColor(0x404040)
                        .waterColor(0x101010)
                        .foliageColor(0)
                        .waterFogColor(0)
                        .moodSound(new BiomeMoodSound(ModSoundEvents.CRACK, 6000, 8, 2.0))
						.music(new MusicSound(ModSoundEvents.CREEPY, 0, 0, true))
                        .skyColor(0x404040)
                        .grassColor(0)
						.particleConfig(new BiomeParticleConfig(ModParticleTypes.LIMBO_ASH, 0.118093334F))
                        .build()
				)
                .generationSettings(new GenerationSettings.Builder()
						.feature(
								GenerationStep.Feature.SURFACE_STRUCTURES,
								ModFeatures.Placed.LIMBO_GATEWAY_PLACED_FEATURE
						)
						.feature(
								GenerationStep.Feature.UNDERGROUND_ORES,
								ModFeatures.Placed.SOLID_STATIC_ORE_PLACED_FEATURE
						)
						.feature(
								GenerationStep.Feature.UNDERGROUND_ORES,
								ModFeatures.Placed.DECAYED_BLOCK_ORE_PLACED_FEATURE
						)
                        .build()
				)
                .precipitation(Biome.Precipitation.NONE)
                .spawnSettings(new SpawnSettings.Builder()
						.creatureSpawnProbability(0.02f)
						.spawn(
								SpawnGroup.MONSTER,
								new SpawnSettings.SpawnEntry(
										ModEntityTypes.MONOLITH,
										100,
										1,
										1
								)
						)
                        .build()
				)
                .temperature(0.2f)
                .temperatureModifier(Biome.TemperatureModifier.NONE)
                .build();
    }
}
