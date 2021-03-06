package org.dimdev.dimdoors.world.feature.gateway;

import java.util.Set;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;

public interface Gateway {
    void generate(StructureWorldAccess world, BlockPos pos);

    default boolean isBiomeValid(RegistryKey<Biome> biome) {
        return this.getBiomes().contains(biome);
    }

    default boolean isLocationValid(StructureWorldAccess world, BlockPos pos) {
        return this.isBiomeValid(world.getBiomeKey(pos).orElseThrow(NullPointerException::new));
    }

    Set<RegistryKey<Biome>> getBiomes();
}
