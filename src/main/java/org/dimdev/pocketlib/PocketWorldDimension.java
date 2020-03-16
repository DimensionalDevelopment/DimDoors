package org.dimdev.pocketlib;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.dimdev.dimdoors.world.limbo.LimboChunkGeneratorConfig;

public abstract class PocketWorldDimension extends Dimension {
    public PocketWorldDimension(World world, DimensionType dimensionType, float f) {
        super(world, dimensionType, f);
    }

    @Override
    public ChunkGenerator<?> createChunkGenerator() {
        FixedBiomeSourceConfig biomeConfig = BiomeSourceType.FIXED.getConfig(this.world.getSeed()).setBiome(getBiome());
        FixedBiomeSource biomeSource = BiomeSourceType.FIXED.applyConfig(biomeConfig);
        LimboChunkGeneratorConfig chunkGeneratorConfig = new LimboChunkGeneratorConfig();
        return new BlankChunkGenerator(world, biomeSource, new BlankChunkGeneratorConfig());
    }

    @Override
    public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean bl) {
        return null;
    }

    @Override
    public BlockPos getTopSpawningBlockPosition(int i, int j, boolean bl) {
        return null;
    }

    @Override
    public float getSkyAngle(long l, float f) {
        return 0;
    }

    @Override
    public boolean hasVisibleSky() {
        return false;
    }

    @Override
    public Vec3d modifyFogColor(Vec3d vec3d, float f) {
        return null;
    }

    @Override
    public boolean canPlayersSleep() {
        return false;
    }

    @Override
    public boolean isFogThick(int i, int j) {
        return true;
    }


    protected abstract Biome getBiome();
}
