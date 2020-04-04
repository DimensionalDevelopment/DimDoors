package org.dimdev.dimdoors.world.limbo;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.ModDimensions;


public class LimboDimension extends Dimension {
    public LimboDimension(World world, DimensionType dimensionType) {
        super(world, dimensionType, 0);
    }

    @Override
    public ChunkGenerator<?> createChunkGenerator() {
        FixedBiomeSourceConfig biomeConfig = BiomeSourceType.FIXED.getConfig(world.getSeed()).setBiome(ModBiomes.LIMBO);
        FixedBiomeSource biomeSource = BiomeSourceType.FIXED.applyConfig(biomeConfig);
        LimboChunkGeneratorConfig chunkGeneratorConfig = new LimboChunkGeneratorConfig();
        chunkGeneratorConfig.setDefaultBlock(ModBlocks.UNRAVELLED_FABRIC.getDefaultState());
        chunkGeneratorConfig.setDefaultFluid(ModBlocks.ETERNAL_FLUID.getDefaultState());
        return new LimboChunkGenerator(world, biomeSource, chunkGeneratorConfig);
    }

    @Override
    public BlockPos getSpawningBlockInChunk(ChunkPos chunk, boolean bl) {
        return null;
    }

    @Override
    public BlockPos getTopSpawningBlockPosition(int i, int j, boolean bl) {
        return null;
    }

    @Override
    public float getSkyAngle(long l, float f) {
        return 0.5f;
    }

    @Override
    public boolean hasVisibleSky() {
        return false;
    }

    @Override
    public Vec3d modifyFogColor(Vec3d vec3d, float f) {
        return new Vec3d(0.2,0.2,0.2);
    }

    @Override
    public boolean canPlayersSleep() {
        return false;
    }

    @Override
    public boolean isFogThick(int i, int j) {
        return false;
    }

    @Override
    public DimensionType getType() {
        return ModDimensions.LIMBO;
    }
}
