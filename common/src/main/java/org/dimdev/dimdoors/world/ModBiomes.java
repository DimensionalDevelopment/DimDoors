package org.dimdev.dimdoors.world;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.particle.ModParticleTypes;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.carvers.ModCarvers;
import org.dimdev.dimdoors.world.feature.ModFeatures;

import static org.dimdev.dimdoors.DimensionalDoors.id;

public final class ModBiomes {
    public static final ResourceKey<Biome> PERSONAL_WHITE_VOID_KEY = register("white_void");
    public static final ResourceKey<Biome> PUBLIC_BLACK_VOID_KEY = register("black_void");
    public static final ResourceKey<Biome> DUNGEON_DANGEROUS_BLACK_VOID_KEY = register("dangerous_black_void");
    public static final ResourceKey<Biome> LIMBO_KEY = register("limbo");

    public static void init() {
    }

	private static ResourceKey<Biome> register(String name) {
		return ResourceKey.create(Registries.BIOME, id(name));
	}

    public static void bootstrap(BootstrapContext<Biome> entries) {
        entries.register(LIMBO_KEY, new Biome.BiomeBuilder()
                .downfall(0.0f).hasPrecipitation(false)
                .temperatureAdjustment(Biome.TemperatureModifier.NONE)
                        .temperature(0.8f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(0x404040)
                        .waterColor(0x101010)
                        .waterFogColor(0)
                        .foliageColorOverride(0)
                        .skyColor(0x404040)
                        .grassColorOverride(0x404040)
                        .ambientMoodSound(new AmbientMoodSettings(
                                BuiltInRegistries.SOUND_EVENT.getHolderOrThrow(ModSoundEvents.CRACK.getKey()),
                                6000,
                                8,
                                2
                        ))
                        .backgroundMusic(new Music(
                                BuiltInRegistries.SOUND_EVENT.getHolderOrThrow(ModSoundEvents.CREEPY.getKey()),
                                0,
                                120000,
                                true
                        ))
                        .ambientParticle(new AmbientParticleSettings(
                                ModParticleTypes.LIMBO_ASH.get(),
                                0.118093334f
                        )).build())
                        .generationSettings(new BiomeGenerationSettings.PlainBuilder()
                                .addCarver(GenerationStep.Carving.AIR, entries.lookup(Registries.CONFIGURED_CARVER).getOrThrow(ModCarvers.LIMBO))
                                .addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, entries.lookup(Registries.PLACED_FEATURE).getOrThrow(ModFeatures.Placed.SOLID_STATIC_ORE))
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(
                                        ModEntityTypes.MONOLITH.get(),
                                        100,
                                        1,
                                        10
                                )).build())
                .build());
        var voidBiome = new Biome.BiomeBuilder()
                .downfall(0)
                .temperature(0.8f)
                .hasPrecipitation(false)
                .temperatureAdjustment(Biome.TemperatureModifier.NONE)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3f76e4)
                        .waterFogColor(0x50533)
                        .fogColor(0)
                        .skyColor(0)
                        .grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE)
                        .build())
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .generationSettings(BiomeGenerationSettings.EMPTY);

        entries.register(PUBLIC_BLACK_VOID_KEY, voidBiome.build());
        entries.register(DUNGEON_DANGEROUS_BLACK_VOID_KEY, voidBiome.build());

        entries.register(PERSONAL_WHITE_VOID_KEY, new Biome.BiomeBuilder()
                .downfall(0)
                .temperature(0.8f)
                .temperatureAdjustment(Biome.TemperatureModifier.NONE)
                .hasPrecipitation(false)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3f76e4)
                        .waterFogColor(0x50533)
                        .fogColor(0xffffff)
                        .skyColor(0xffffff)
                        .grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE)
                        .backgroundMusic(
                                new Music(
                                BuiltInRegistries.SOUND_EVENT.getHolderOrThrow(ModSoundEvents.WHITE_VOID.getKey()),
                                0,
                                0,
                                true
                        )).build())
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .build());
    }
}
