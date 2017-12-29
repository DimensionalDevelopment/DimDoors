package org.dimdev.dimdoors.shared.world.limbodimension;

import org.dimdev.dimdoors.shared.blocks.BlockFabric;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.world.ModBiomes;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LimboGenerator implements IChunkGenerator { // TODO: make limbo a biome that can also be outside the limbo world

    private Random rand;

    // Noise generators
    private NoiseGeneratorOctaves minLimitPerlinNoise;
    private NoiseGeneratorOctaves maxLimitPerlinNoise;
    private NoiseGeneratorOctaves mainPerlinNoise;
    private NoiseGeneratorOctaves surfaceNoise;
    public NoiseGeneratorOctaves scaleNoise;
    public NoiseGeneratorOctaves depthNoise;
    public NoiseGeneratorOctaves mobSpawnerNoise; // TODO: right name?

    // Noise regions
    double[] mainNoiseRegion;
    double[] minLimitRegion;
    double[] maxLimitRegion;
    double[] scaleNoseRegion; // TODO: right name?

    private World world;
    private double[] noiseArray;

    /**
     * The biomes that are used to generate the chunk
     */
    private Biome[] biomesForGeneration = {ModBiomes.LIMBO};


    /**
     * A double array that holds terrain noise from depthNoise
     */
    double[] depthRegion;

    /**
     * Used to store the 5x5 parabolic field that is used during terrain generation.
     */
    float[] parabolicField;
    int[][] field_73219_j = new int[32][32];

    public LimboGenerator(World world, long seed) {
        this.world = world;
        rand = new Random(seed);
        minLimitPerlinNoise = new NoiseGeneratorOctaves(rand, 16); //base terrain
        maxLimitPerlinNoise = new NoiseGeneratorOctaves(rand, 16); //hillyness
        mainPerlinNoise = new NoiseGeneratorOctaves(rand, 80);  //seems to adjust the size of features, how stretched things are -default 8
        surfaceNoise = new NoiseGeneratorOctaves(rand, 4);
        scaleNoise = new NoiseGeneratorOctaves(rand, 10);
        depthNoise = new NoiseGeneratorOctaves(rand, 16);
        mobSpawnerNoise = new NoiseGeneratorOctaves(rand, 8); // TODO: is this named right?

        this.world = world;
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        rand.setSeed(x * 341873128712L + z * 132897987541L);
        ChunkPrimer primer = new ChunkPrimer();
        setBlocksInChunk(x, z, primer);
        Chunk chunk = new Chunk(world, primer, x, z);
        chunk.generateSkylightMap();

        if (!chunk.isTerrainPopulated()) {
            chunk.setTerrainPopulated(true);
            //spawner.registerChunkForPopulation(properties.LimboDimensionID, chunkX, chunkZ);
        }

        return chunk;
    }

    @Override
    public void populate(int x, int z) {

    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    private double[] initializeNoiseField(double[] par1ArrayOfDouble, int par2, int par3, int par4, int par5, int par6, int par7) {
        if (par1ArrayOfDouble == null) {
            par1ArrayOfDouble = new double[par5 * par6 * par7];
        }

        if (parabolicField == null) {
            parabolicField = new float[25];

            for (int var8 = -2; var8 <= 2; ++var8) {
                for (int var9 = -2; var9 <= 2; ++var9) {
                    float var10 = 10.0F / MathHelper.sqrt(var8 * var8 + var9 * var9 + 0.2F);
                    parabolicField[var8 + 2 + (var9 + 2) * 5] = var10;
                }
            }
        }

        double var44 = 884.412D; //large values here create spiky land. add a 0, good -default 884
        double var45 = 9840.412D; //large values here make sheets- default - 684
        scaleNoseRegion = scaleNoise.generateNoiseOctaves(scaleNoseRegion, par2, par4, par5, par7, 1.121D, 1.121D, 0.5D);
        depthRegion = depthNoise.generateNoiseOctaves(depthRegion, par2, par4, par5, par7, 200.0D, 200.0D, 0.5D);
        mainNoiseRegion = mainPerlinNoise.generateNoiseOctaves(mainNoiseRegion, par2, par3, par4, par5, par6, par7, var44 / 80.0D, var45 / 160.0D, var44 / 80.0D);
        minLimitRegion = minLimitPerlinNoise.generateNoiseOctaves(minLimitRegion, par2, par3, par4, par5, par6, par7, var44, var45, var44);
        maxLimitRegion = maxLimitPerlinNoise.generateNoiseOctaves(maxLimitRegion, par2, par3, par4, par5, par6, par7, var44, var45, var44);

        int var12 = 0;
        int var13 = 0;

        for (int var14 = 0; var14 < par5; ++var14) {
            for (int var15 = 0; var15 < par7; ++var15) {
                float var16 = 0.0F;
                float var17 = 0.0F;
                float var18 = 0.0F;
                byte var19 = 2;

                for (int var21 = -var19; var21 <= var19; ++var21) {
                    for (int var22 = -var19; var22 <= var19; ++var22) {
                        float var24 = parabolicField[var21 + 2 + (var22 + 2) * 5] / (Biomes.PLAINS.getBaseHeight() + 9.0F);


                        //this adjusts the height of the terrain

                        var16 += Biomes.PLAINS.getHeightVariation() * var24 + 4;
                        var17 += Biomes.PLAINS.getBaseHeight() * var24 - 1;
                        var18 += var24;
                    }
                }

                var16 /= var18;
                var17 /= var18;
                var16 = var16 * 0.9F + 0.1F;
                var17 = (var17 * 4.0F - 1.0F) / 8.0F;
                double var47 = depthRegion[var13] / 8000.0D;

                if (var47 < 0.0D) {
                    var47 = -var47 * 0.3D;
                }

                var47 = var47 * 3.0D - 2.0D;

                if (var47 < 0.0D) {
                    var47 /= 2.0D;

                    if (var47 < -1.0D) {
                        var47 = -1.0D;
                    }

                    var47 /= 1.4D;
                    var47 /= 2.0D;
                } else {
                    if (var47 > 1.0D) {
                        var47 = 1.0D;
                    }

                    var47 /= 8.0D;
                }

                ++var13;

                for (int var46 = 0; var46 < par6; ++var46) {
                    double var48 = var17;
                    var48 += var47 * 0.2D;
                    var48 = var48 * par6 / 16.0D;
                    double var28 = par6 / 2.0D + var48 * 4.0D;
                    double var30;
                    double var32 = (var46 - var28) * 12.0D * 128.0D / 128.0D / (double) var16;

                    if (var32 < 0.0D) {
                        var32 *= 4.0D;
                    }

                    double var34 = minLimitRegion[var12] / 512.0D;
                    double var36 = maxLimitRegion[var12] / 512.0D;
                    double var38 = (mainNoiseRegion[var12] / 10.0D + 1.0D) / 2.0D;

                    if (var38 < 0.0D) {
                        var30 = var34;
                    } else if (var38 > 1.0D) {
                        var30 = var36;
                    } else {
                        var30 = var34 + (var36 - var34) * var38;
                    }

                    var30 -= var32;

                    if (var46 > par6 - 4) {
                        double var40 = (var46 - (par6 - 4)) / 3.0F;
                        var30 = var30 * (1.0D - var40) + -10.0D * var40;
                    }

                    par1ArrayOfDouble[var12] = var30;
                    ++var12;
                }
            }
        }

        return par1ArrayOfDouble;
    }

    public void setBlocksInChunk(int x, int z, ChunkPrimer primer) {
        biomesForGeneration = world.getBiomeProvider().getBiomesForGeneration(biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10);
        noiseArray = initializeNoiseField(noiseArray, x * 4, 0, z * 4, 5, 17, 5);

        int xzSections = 4;
        int xzSectionSize = 4;
        int ySections = 16;
        int ySectionSize = 8;

        double xzScale = 1.0 / xzSectionSize;
        double yScale = 1.0 / ySectionSize;
        for (int sectionX = 0; sectionX < xzSections; ++sectionX) {
            int xSectionPart = sectionX * xzSectionSize;
            int i0__ = sectionX * (xzSections + 1);
            int i1__ = (sectionX + 1) * (xzSections + 1);

            for (int sectionZ = 0; sectionZ < xzSections; ++sectionZ) {
                int zSectionPart = sectionZ * xzSectionSize;
                int i0_0 = (i0__ + sectionZ) * (ySections + 1);
                int i0_1 = (i0__ + sectionZ + 1) * (ySections + 1);
                int i1_0 = (i1__ + sectionZ) * (ySections + 1);
                int i1_1 = (i1__ + sectionZ + 1) * (ySections + 1);

                for (int sectionY = 0; sectionY < ySections; ++sectionY) {
                    int ySectionPart = sectionY * ySectionSize;
                    double v0y0 = noiseArray[i0_0 + sectionY];
                    double v0y1 = noiseArray[i0_1 + sectionY];
                    double v1y0 = noiseArray[i1_0 + sectionY];
                    double v1y1 = noiseArray[i1_1 + sectionY];
                    double d0y0 = (noiseArray[i0_0 + sectionY + 1] - v0y0) * yScale;
                    double d0y1 = (noiseArray[i0_1 + sectionY + 1] - v0y1) * yScale;
                    double d1y0 = (noiseArray[i1_0 + sectionY + 1] - v1y0) * yScale;
                    double d1y1 = (noiseArray[i1_1 + sectionY + 1] - v1y1) * yScale;

                    for (int yRel = 0; yRel < ySectionSize; ++yRel) {
                        int yCoord = ySectionPart + yRel;
                        double vxy0 = v0y0;
                        double vxy1 = v0y1;
                        double dxy0 = (v1y0 - v0y0) * xzScale;
                        double dxy1 = (v1y1 - v0y1) * xzScale;

                        for (int xRel = 0; xRel < xzSectionSize; ++xRel) {
                            int xCoord = xSectionPart + xRel;
                            double dxyz = (vxy1 - vxy0) * xzScale;
                            double vxyz = vxy0 - dxyz;

                            for (int zRel = 0; zRel < xzSectionSize; ++zRel) {
                                int zCoord = zSectionPart + zRel;
                                if (vxyz > 0) {
                                    primer.setBlockState(xCoord, yCoord, zCoord,
                                            ModBlocks.FABRIC.getDefaultState().withProperty(BlockFabric.TYPE, BlockFabric.EnumType.UNRAVELED));
                                } else if (yCoord < 6) {
                                    primer.setBlockState(xCoord, yCoord, zCoord,
                                            ModBlocks.FABRIC.getDefaultState().withProperty(BlockFabric.TYPE, BlockFabric.EnumType.ANCIENT));
                                }
                            }

                            vxy0 += dxy0;
                            vxy1 += dxy1;
                        }

                        v0y0 += d0y0;
                        v0y1 += d0y1;
                        v1y0 += d1y0;
                        v1y1 += d1y1;
                    }
                }
            }
        }
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return new ArrayList<>();
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {

    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }
}
