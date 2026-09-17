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
}
