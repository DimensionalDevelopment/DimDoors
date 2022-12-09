package org.dimdev.dimdoors.world;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.Biome;

import static org.dimdev.dimdoors.DimensionalDoors.id;

public final class ModBiomes {
    public static final RegistryKey<Biome> PERSONAL_WHITE_VOID_KEY = register("white_void");
    public static final RegistryKey<Biome> PUBLIC_BLACK_VOID_KEY = register("black_void");
    public static final RegistryKey<Biome> DUNGEON_DANGEROUS_BLACK_VOID_KEY = register("dangerous_black_void");
    public static final RegistryKey<Biome> LIMBO_KEY = register("limbo");

    public static void init() {
    }

	private static RegistryKey<Biome> register(String name) {
		return RegistryKey.of(RegistryKeys.BIOME, id(name));
	}
}
