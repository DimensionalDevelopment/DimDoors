package org.dimdev.dimdoors.world.feature.gateway;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public class SandstonePillarsGateway extends SchematicGateway {
    public SandstonePillarsGateway() {
        super("sandstone_pillars");
    }

    @Override
    public Biome[] getBiomes() {
        return new Biome[]{BuiltinRegistries.BIOME.get(BiomeKeys.DESERT), BuiltinRegistries.BIOME.get(BiomeKeys.DESERT_LAKES), BuiltinRegistries.BIOME.get(BiomeKeys.DESERT_HILLS)};
    }
}
