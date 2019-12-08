package org.dimdev.dimdoors.world.limbo;

import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

import java.util.Random;

public class LimboChunkGenerator implements SurfaceChunkGenerator<LimboChunkGeneratorConfig> {
    private final double[] noiseFalloff = this.buildNoiseFalloff();

    private double[] buildNoiseFalloff() {
        return new double[0];
    }

//
//    @Override
//    public int getHeightOnGround(int x, int z, Heightmap.Type type) {
//        double xzScale = 884.412D; //large values here create spiky land. add a 0, good -default 884, these are configurable in vanilla
//        double yScale = 9840.412D; //large values here make sheets- default - 684
//
//        depthRegion = depthNoise.generateNoiseOctaves(depthRegion, xOffset, zOffset, xSize, zSize, 200.0D, 200.0D, 0.5D);
//        mainNoiseRegion = mainPerlinNoise.generateNoiseOctaves(mainNoiseRegion, xOffset, yOffset, zOffset, xSize, ySize, zSize, xzScale / 80.0D, yScale / 160.0D, xzScale / 80.0D);
//        minLimitRegion = minLimitPerlinNoise.generateNoiseOctaves(minLimitRegion, xOffset, yOffset, zOffset, xSize, ySize, zSize, xzScale, yScale, xzScale);
//        maxLimitRegion = maxLimitPerlinNoise.generateNoiseOctaves(maxLimitRegion, xOffset, yOffset, zOffset, xSize, ySize, zSize, xzScale, yScale, xzScale);
//
//        int heightMapIndex = 0;
//        int depthRegionIndex = 0;
//        for (int x = 0; x < xSize; x++) {
//            for (int z = 0; z < zSize; z++) {
//                // These were were static but calculated by some wrongly-converted biome blend code (which is unnecessary
//                // since there is only one biome in limbo)
//                float heightVariation = 5.959498f;
//                float baseHeight = -1.3523747f;
//
//                heightVariation = heightVariation * 0.9F + 0.1F;
//                baseHeight = (baseHeight * 4.0F - 1.0F) / 8.0F;
//
//                double depthOffset = depthRegion[depthRegionIndex] / 8000.0D; // TODO: is depthOffset a right name?
//                // Transform depthOffset based on this function (https://goo.gl/ZDXra8)
//                // Values of d outside the interval (-10/3, 1) are clamped to 1/8
//                // The range is [-5/14, 1/8], with the minimum being at 0
//                if (depthOffset < 0.0D) depthOffset = -depthOffset * 0.3D;
//                depthOffset = depthOffset * 3.0D - 2.0D;
//                if (depthOffset < 0.0D) {
//                    depthOffset /= 2.0D;
//                    if (depthOffset < -1.0D) depthOffset = -1.0D; // Not possible
//                    depthOffset /= 1.4D;
//                    depthOffset /= 2.0D;
//                } else {
//                    if (depthOffset > 1.0D) depthOffset = 1.0D;
//                    depthOffset /= 8.0D;
//                }
//                depthRegionIndex++;
//
//                for (int y = 0; y < ySize; y++) {
//                    double depth = baseHeight;
//                    depth += depthOffset * 0.2D;
//                    depth = depth * ySize / 16.0D;
//                    depth = ySize / 2.0D + depth * 4.0D; // This was a separate double var28 in vanilla
//
//                    // TODO: is heightOffset named right?
//                    double heightOffset = (y - depth) * 12.0D * 128.0D / 128.0D / (double) heightVariation; // Vanilla was * 128D / 256D, configurable
//
//                    if (heightOffset < 0.0D) {
//                        heightOffset *= 4.0D;
//                    }
//
//                    double minLimit = minLimitRegion[heightMapIndex] / 512.0D;
//                    double maxLimit = maxLimitRegion[heightMapIndex] / 512.0D;
//                    double mainNoise = (mainNoiseRegion[heightMapIndex] / 10.0D + 1.0D) / 2.0D;
//
//                    double height;
//                    if (mainNoise < 0.0D) {
//                        height = minLimit;
//                    } else if (mainNoise > 1.0D) {
//                        height = maxLimit;
//                    } else {
//                        height = minLimit + (maxLimit - minLimit) * mainNoise;
//                    }
//
//                    height -= heightOffset;
//
//                    if (y > ySize - 4) {
//                        double var40 = (y - (ySize - 4)) / 3.0F; // TODO: what does this do?
//                        height = height * (1.0D - var40) + -10.0D * var40;
//                    }
//
//                    heightMap[heightMapIndex] = height;
//                    heightMapIndex++;
//                }
//            }
//        }
//
//        return heightMap;
//    }


    //
    private Random rand;
    //
//    // Noise generators
    private OctavePerlinNoiseSampler minLimitPerlinNoise;
    private OctavePerlinNoiseSampler maxLimitPerlinNoise;
    private OctavePerlinNoiseSampler mainPerlinNoise;
    public OctavePerlinNoiseSampler depthNoise;
    //
    double[] mainNoiseRegion;
    double[] minLimitRegion;
    double[] maxLimitRegion;

    private World world;
    private double[] heightMap;

    double[] depthRegion;

    @Override
    protected double[] computeNoiseRange(int i, int j) {
        return new double[]{0, 0};
    }

    @Override
    protected double computeNoiseFalloff(double d, double e, int i) {
        return 0;
    }

    @Override
    protected void sampleNoiseColumn(double[] ds, int i, int j) {

    }

    @Override
    public int getSpawnHeight() {
        return 0;
    }

//
//    public LimboChunkGenerator(World world, long seed) {
//        this.world = world;
//        rand = new Random(seed);
//        minLimitPerlinNoise = new NoiseGeneratorOctaves(rand, 16); //base terrain
//        maxLimitPerlinNoise = new NoiseGeneratorOctaves(rand, 16); //hillyness
//        mainPerlinNoise = new NoiseGeneratorOctaves(rand, 80);  //seems to adjust the size of features, how stretched things are -default 8
//        depthNoise = new NoiseGeneratorOctaves(rand, 16);
//
//        this.world = world;
//    }
//
//    @Override
//    public Chunk generateChunk(int x, int z) {
//        rand.setSeed(x * 341873128712L + z * 132897987541L);
//        ChunkPrimer primer = new ChunkPrimer();
//        setBlocksInChunk(x, z, primer);
//        Chunk chunk = new Chunk(world, primer, x, z);
//        chunk.generateSkylightMap();
//
//        if (!chunk.isTerrainPopulated()) {
//            chunk.setTerrainPopulated(true);
//        }
//
//        return chunk;
//    }
//
//    @Override
//    public void populate(int x, int z) {
//        Biome biome = world.getBiome(new BlockPos(x * 16 + 16, 0, z * 16 + 16));
//        WorldEntitySpawner.performWorldGenSpawning(world, biome, x * 16 + 8, z * 16 + 8, 16, 16, rand);
//    }
//
//    @Override
//    public boolean generateStructures(Chunk chunk, int x, int z) {
//        return false;
//    }
//
//    private double[] generateHeightmap(double[] heightMap, int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize) {

//    }
//
//    @SuppressWarnings("LocalVariableNamingConvention")
//    public void setBlocksInChunk(int x, int z, ChunkPrimer primer) {
//        biomesForGeneration = world.getBiomeProvider().getBiomesForGeneration(biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10);
//        heightMap = generateHeightmap(heightMap, x * 4, 0, z * 4, 5, 17, 5); // vanilla ySize is 33
//
//        int xzSectionCount = 4;
//        int xzSectionSize = 4;
//        int ySectionCount = 16;
//        int ySectionSize = 8;
//
//        double xzScale = 1.0 / xzSectionSize;
//        double yScale = 1.0 / ySectionSize;
//        for (int sectionX = 0; sectionX < xzSectionCount; sectionX++) {
//            int xSectionPart = sectionX * xzSectionSize;
//            int i0__ = sectionX * (xzSectionCount + 1);
//            int i1__ = (sectionX + 1) * (xzSectionCount + 1);
//
//            for (int sectionZ = 0; sectionZ < xzSectionCount; sectionZ++) {
//                int zSectionPart = sectionZ * xzSectionSize;
//                int i0_0 = (i0__ + sectionZ) * (ySectionCount + 1);
//                int i0_1 = (i0__ + sectionZ + 1) * (ySectionCount + 1);
//                int i1_0 = (i1__ + sectionZ) * (ySectionCount + 1);
//                int i1_1 = (i1__ + sectionZ + 1) * (ySectionCount + 1);
//
//                for (int sectionY = 0; sectionY < ySectionCount; sectionY++) {
//                    int ySectionPart = sectionY * ySectionSize;
//                    double v0y0 = heightMap[i0_0 + sectionY];
//                    double v0y1 = heightMap[i0_1 + sectionY];
//                    double v1y0 = heightMap[i1_0 + sectionY];
//                    double v1y1 = heightMap[i1_1 + sectionY];
//                    double d0y0 = (heightMap[i0_0 + sectionY + 1] - v0y0) * yScale;
//                    double d0y1 = (heightMap[i0_1 + sectionY + 1] - v0y1) * yScale;
//                    double d1y0 = (heightMap[i1_0 + sectionY + 1] - v1y0) * yScale;
//                    double d1y1 = (heightMap[i1_1 + sectionY + 1] - v1y1) * yScale;
//
//                    for (int yRel = 0; yRel < ySectionSize; ++yRel) {
//                        int yCoord = ySectionPart + yRel;
//                        double vxy0 = v0y0;
//                        double vxy1 = v0y1;
//                        double dxy0 = (v1y0 - v0y0) * xzScale;
//                        double dxy1 = (v1y1 - v0y1) * xzScale;
//
//                        for (int xRel = 0; xRel < xzSectionSize; ++xRel) {
//                            int xCoord = xSectionPart + xRel;
//                            double dxyz = (vxy1 - vxy0) * xzScale;
//                            double vxyz = vxy0 - dxyz;
//
//                            for (int zRel = 0; zRel < xzSectionSize; ++zRel) {
//                                int zCoord = zSectionPart + zRel;
//                                if (vxyz > 0) {
//                                    primer.setBlockState(xCoord, yCoord, zCoord, ModBlocks.UNRAVELLED_FABRIC.getDefaultState());
//                                } else if (yCoord < 6) {
//                                    primer.setBlockState(xCoord, yCoord, zCoord, ModBlocks.ETERNAL_FABRIC.getDefaultState());
//                                }
//                            }
//
//                            vxy0 += dxy0;
//                            vxy1 += dxy1;
//                        }
//
//                        v0y0 += d0y0;
//                        v0y1 += d0y1;
//                        v1y0 += d1y0;
//                        v1y1 += d1y1;
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
//        Biome biome = world.getBiome(pos);
//        return biome.getSpawnableList(creatureType);
//    }
//
//    @Nullable
//    @Override
//    public BlockPos getNearestStructurePos(World world, String structureName, BlockPos position, boolean findUnexplored) {
//        return null;
//    }
//
//    @Override
//    public void recreateStructures(Chunk chunk, int x, int z) {
//
//    }
//
//    @Override
//    public boolean isInsideStructure(World world, String structureName, BlockPos pos) {
//        return false;
//    }
}
