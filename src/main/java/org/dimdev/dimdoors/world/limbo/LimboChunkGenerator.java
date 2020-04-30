package org.dimdev.dimdoors.world.limbo;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.util.List;

public class LimboChunkGenerator extends SurfaceChunkGenerator<LimboChunkGeneratorConfig> {
    private final double[] noiseFalloff = buildNoiseFalloff();

    public LimboChunkGenerator(IWorld world, BiomeSource biomeSource, LimboChunkGeneratorConfig config) {
        super(world, biomeSource, 4, 8, 256, config, false);
    }

    @Override
    protected void sampleNoiseColumn(double[] buffer, int x, int z) {
        sampleNoiseColumn(buffer, x, z, 884.412D, 9740.421D, 8.555150000000001D, 20.2206D, 3, -10);
    }

    @Override
    protected double[] computeNoiseRange(int i, int j) {
        return new double[] {-0.80118735, 5.4635482};
    }

    @Override
    protected double computeNoiseFalloff(double d, double e, int i) {
        return noiseFalloff[i]/8000d;
    }

    private double[]  buildNoiseFalloff() {
        double[] ds = new double[getNoiseSizeY()];

        for (int i = 0; i < getNoiseSizeY(); ++i) {
            ds[i] = Math.cos((double) i * 3.141592653589793D * 6.0D / (double) getNoiseSizeY()) * 2.0D;
            double d = i;
            if (i > getNoiseSizeY() / 2) {
                d = getNoiseSizeY() - 1 - i;
            }

            if (d < 4.0D) {
                d = 4.0D - d;
                ds[i] -= d * d * d * 10.0D;
            }
        }

        return ds;
    }

    @Override
    public List<Biome.SpawnEntry> getEntitySpawnList(EntityCategory entityCategory, BlockPos blockPos) {
        if (entityCategory == EntityCategory.MONSTER) {
            if (Feature.NETHER_BRIDGE.isInsideStructure(world, blockPos)) {
                return Feature.NETHER_BRIDGE.getMonsterSpawns();
            }

            if (Feature.NETHER_BRIDGE.isApproximatelyInsideStructure(world, blockPos) && world.getBlockState(blockPos.down()).getBlock() == Blocks.NETHER_BRICKS) {
                return Feature.NETHER_BRIDGE.getMonsterSpawns();
            }
        }

        return super.getEntitySpawnList(entityCategory, blockPos);
    }

    @Override
    public int getSpawnHeight() {
        return world.getSeaLevel() + 1;
    }

    @Override
    public int getMaxY() {
        return 256;
    }

    @Override
    public int getSeaLevel() {
        return 32;
    }
}
