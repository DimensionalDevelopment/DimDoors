package org.dimdev.dimdoors.world.feature.gateway;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public abstract class BaseGateway {
    public void generate(StructureWorldAccess world, int x, int y, int z) {
    }

    protected boolean isBiomeValid(RegistryKey<Biome> biome) {
        return this.getBiomes().contains(biome);
    }

    public boolean isLocationValid(World world, int x, int y, int z) {
        return this.isBiomeValid(BuiltinRegistries.BIOME.getKey(world.getBiome(new BlockPos(x, y, z))).orElseThrow(NullPointerException::new));
    }

    public Set<RegistryKey<Biome>> getBiomes() {
        return ImmutableSet.of();
    }
}
