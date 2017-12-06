package com.zixiken.dimdoors.shared.world.biomes;

import net.minecraft.world.biome.Biome;

/**
 * Created by Jared Johnson on 1/24/2017.
 */
public class DimDoorsBiome extends Biome {

    public DimDoorsBiome(String name) {
        super(new BiomeProperties(name));
        decorator.treesPerChunk = 0;
        decorator.flowersPerChunk = 0;
        decorator.grassPerChunk = 0;

        spawnableMonsterList.clear();
        spawnableCreatureList.clear();
        spawnableWaterCreatureList.clear();
        spawnableCaveCreatureList.clear();
    }

    @Override
    public boolean canRain() {
        return false;
    }
}
