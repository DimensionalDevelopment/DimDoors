package org.dimdev.dimdoors.world;

import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.mixin.BuiltinBiomesAccessor;
import org.dimdev.dimdoors.sound.ModSoundEvents;

import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
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
        Registry.register(BuiltinRegistries.BIOME, LIMBO_KEY.getValue(), LIMBO_BIOME);
        Registry.register(BuiltinRegistries.BIOME, PERSONAL_WHITE_VOID_KEY.getValue(), WHITE_VOID_BIOME);
        Registry.register(BuiltinRegistries.BIOME, PUBLIC_BLACK_VOID_KEY.getValue(), BLACK_VOID_BIOME);
        Registry.register(BuiltinRegistries.BIOME, DUNGEON_DANGEROUS_BLACK_VOID_KEY.getValue(), DANGEROUS_BLACK_VOID_BIOME);
        BuiltinBiomesAccessor.getIdMap().put(BuiltinRegistries.BIOME.getRawId(LIMBO_BIOME), LIMBO_KEY);
        BuiltinBiomesAccessor.getIdMap().put(BuiltinRegistries.BIOME.getRawId(DANGEROUS_BLACK_VOID_BIOME), DUNGEON_DANGEROUS_BLACK_VOID_KEY);
        BuiltinBiomesAccessor.getIdMap().put(BuiltinRegistries.BIOME.getRawId(BLACK_VOID_BIOME), PUBLIC_BLACK_VOID_KEY);
        BuiltinBiomesAccessor.getIdMap().put(BuiltinRegistries.BIOME.getRawId(WHITE_VOID_BIOME), PERSONAL_WHITE_VOID_KEY);
    }

    private static BiomeEffects createEffect(boolean white) {
        BiomeEffects.Builder builder = new BiomeEffects.Builder()
                .waterColor(white ? 0xFFFFFF : 0x000000)
                .waterFogColor(white ? 0xFFFFFF : 0x000000)
                .fogColor(white ? 0xFFFFFF : 0x000000)
                .skyColor(white ? 0xFFFFFF : 0x808080)
                .grassColorModifier(BiomeEffects.GrassColorModifier.NONE);
        if (white) {
            builder.loopSound(ModSoundEvents.WHITE_VOID);
        }
        return builder.build();
    }

    static {
        Biome.Builder voidBiomeBuilder = new Biome.Builder().category(Biome.Category.NONE).depth(0).downfall(0).generationSettings(new GenerationSettings.Builder().surfaceBuilder(SurfaceBuilder.DEFAULT.method_30478(new TernarySurfaceConfig(Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), Blocks.VOID_AIR.getDefaultState()))).build()).precipitation(Biome.Precipitation.NONE).scale(0).spawnSettings(new SpawnSettings.Builder().build()).temperature(0.8f).temperatureModifier(Biome.TemperatureModifier.NONE);
        PERSONAL_WHITE_VOID_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("dimdoors:white_void"));
        PUBLIC_BLACK_VOID_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("dimdoors:black_void"));
        DUNGEON_DANGEROUS_BLACK_VOID_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("dimdoors:dangerous_black_void"));
        LIMBO_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("dimdoors", "limbo"));
        WHITE_VOID_BIOME = voidBiomeBuilder.effects(createEffect(true)).build();
        BLACK_VOID_BIOME = voidBiomeBuilder.effects(createEffect(false)).build();
        DANGEROUS_BLACK_VOID_BIOME =voidBiomeBuilder.effects(createEffect(false)).build();
        LIMBO_BIOME = new Biome.Builder()
                .category(Biome.Category.NONE)
                .depth(0.1f)
                .downfall(0.0f)
                .effects(new BiomeEffects.Builder()
                        .fogColor(0x111111)
                        .waterColor(0)
                        .foliageColor(0)
                        .waterFogColor(0)
                        .moodSound(new BiomeMoodSound(ModSoundEvents.CREEPY, 6000, 8, 2.0))
                        .skyColor(0x111111)
                        .grassColor(0)
                        .build())
                .generationSettings(new GenerationSettings.Builder()
                        .surfaceBuilder(SurfaceBuilder.NETHER.method_30478(new TernarySurfaceConfig(ModBlocks.UNRAVELLED_FABRIC.getDefaultState(), ModBlocks.UNRAVELLED_FABRIC.getDefaultState(), ModBlocks.ETERNAL_FLUID.getDefaultState())))
                        .build())
                .precipitation(Biome.Precipitation.NONE)
                .scale(2F)
                .spawnSettings(new SpawnSettings.Builder()
                        .creatureSpawnProbability(0.2f)
                        .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntityTypes.MONOLITH, 1, 1, 1))
                        .build())
                .temperature(0.2f)
                .temperatureModifier(Biome.TemperatureModifier.NONE)
                .build();
    }
}
