package org.dimdev.dimdoors.world.limbo;

import java.util.Collections;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import org.dimdev.dimdoors.world.ModBiomes;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

public class LimboBiomeSource extends BiomeSource {
    public static final Codec<LimboBiomeSource> CODEC = MapCodec.of(Encoder.empty(), Decoder.unit(LimboBiomeSource::new)).stable().codec();

    public LimboBiomeSource() {
        super(Collections.singletonList(ModBiomes.LIMBO_BIOME));
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
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return ModBiomes.LIMBO_BIOME;
    }
}
