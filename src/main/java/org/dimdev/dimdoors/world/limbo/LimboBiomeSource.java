package org.dimdev.dimdoors.world.limbo;

import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import org.dimdev.dimdoors.world.ModBiomes;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

public class LimboBiomeSource extends BiomeSource {
    public static final LimboBiomeSource INSTANCE = new LimboBiomeSource();
    public static final Codec<LimboBiomeSource> CODEC = Codec.unit(INSTANCE);

    private LimboBiomeSource() {
        super(ImmutableList.of(ModBiomes.LIMBO_BIOME));
    }

    @Override
    public Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public BiomeSource withSeed(long seed) {
        return this;
    }

    @Override
    public Set<Biome> getBiomesInArea(int x, int y, int z, int radius) {
        return ImmutableSet.of(ModBiomes.LIMBO_BIOME);
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return ModBiomes.LIMBO_BIOME;
    }
}
