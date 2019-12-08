package org.dimdev.pocketlib;

import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BlankChunkGenerator extends ChunkGenerator<BlankChunkGeneratorConfig> {
    public BlankChunkGenerator(IWorld world, BiomeSource biomeSource, BlankChunkGeneratorConfig config) {
        super(world, biomeSource, config);
    }

    @Override
    public void buildSurface(ChunkRegion chunkRegion, Chunk chunk) {}

    @Override
    public int getSpawnHeight() {
        return 0;
    }

    @Override
    public void populateNoise(IWorld iWorld, Chunk chunk) {}

    @Override
    public int getHeightOnGround(int i, int j, Heightmap.Type type) {
        return 0;
    }
}
