package com.zixiken.dimdoors.shared.world.limbo;

import com.zixiken.dimdoors.shared.blocks.BlockDimWall;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LimboGenerator implements IChunkGenerator
{
    private static Random rand;

    /** A NoiseGeneratorOctaves used in generating terrain */
    private NoiseGeneratorOctaves noiseGen1;

    /** A NoiseGeneratorOctaves used in generating terrain */
    private NoiseGeneratorOctaves noiseGen2;

    /** A NoiseGeneratorOctaves used in generating terrain */
    private NoiseGeneratorOctaves noiseGen3;

    /** A NoiseGeneratorOctaves used in generating terrain */
    private NoiseGeneratorOctaves noiseGen4;

    /** A NoiseGeneratorOctaves used in generating terrain */
    public NoiseGeneratorOctaves noiseGen5;

    public World world;

    /** A NoiseGeneratorOctaves used in generating terrain */
    public NoiseGeneratorOctaves noiseGen6;
    public NoiseGeneratorOctaves mobSpawnerNoise;

    /** Reference to the World object. */
    private World worldObj;

    /** Holds the overall noise array used in chunk generation */
    private double[] noiseArray;

    private MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();

    /** The biomes that are used to generate the chunk */
    private Biome[] biomesForGeneration = { new LimboBiome() };

    /** A double array that hold terrain noise from noiseGen3 */
    double[] noise3;

    /** A double array that hold terrain noise */
    double[] noise1;

    /** A double array that hold terrain noise from noiseGen2 */
    double[] noise2;

    /** A double array that hold terrain noise from noiseGen5 */
    double[] noise5;

    /** A double array that holds terrain noise from noiseGen6 */
    double[] noise6;

    /**
     * Used to store the 5x5 parabolic field that is used during terrain generation.
     */
    float[] parabolicField;
    int[][] field_73219_j = new int[32][32];
    {
        //     caveGenerator = TerrainGen.getModdedMapGen(caveGenerator, CAVE);
    }
    //private CustomLimboPopulator spawner;

    public LimboGenerator(World world, long seed /*CustomLimboPopulator spawner*/) {
        this.worldObj = world;
        LimboGenerator.rand = new Random(seed);
        this.noiseGen1 = new NoiseGeneratorOctaves(LimboGenerator.rand, 16); //base terrain
        this.noiseGen2 = new NoiseGeneratorOctaves(LimboGenerator.rand, 16); //hillyness
        this.noiseGen3 = new NoiseGeneratorOctaves(LimboGenerator.rand, 80);  //seems to adjust the size of features, how stretched things are -default 8
        this.noiseGen4 = new NoiseGeneratorOctaves(LimboGenerator.rand, 4);
        this.noiseGen5 = new NoiseGeneratorOctaves(LimboGenerator.rand, 10);
        this.noiseGen6 = new NoiseGeneratorOctaves(LimboGenerator.rand, 16);
        this.mobSpawnerNoise = new NoiseGeneratorOctaves(LimboGenerator.rand, 8);

        NoiseGeneratorOctaves[] noiseGens = {noiseGen1, noiseGen2, noiseGen3, noiseGen4, noiseGen5, noiseGen6, mobSpawnerNoise};
        //     noiseGens = TerrainGen.getModdedNoiseGenerators(par1World, this.rand, noiseGens);
        this.noiseGen1 = noiseGens[0];
        this.noiseGen2 = noiseGens[1];
        this.noiseGen3 = noiseGens[2];
        this.noiseGen4 = noiseGens[3];
        this.noiseGen5 = noiseGens[4];
        this.noiseGen6 = noiseGens[5];
        this.mobSpawnerNoise = noiseGens[6];

        this.worldObj = world;

        //this.spawner = spawner;
    }

    @Override
    public Chunk provideChunk(int chunkX, int chunkZ)
    {
        //TODO: Wtf? Why do you reinitialize the seed when we already initialized it in the constructor?! ~SenseiKiwi
        LimboGenerator.rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        ChunkPrimer primer = new ChunkPrimer();
        this.scale(chunkX, chunkZ, primer);
        Chunk chunk = new Chunk(this.worldObj, primer, chunkX, chunkZ);
        chunk.generateSkylightMap();

        if (!chunk.isTerrainPopulated()) {
            chunk.setTerrainPopulated(true);
            //spawner.registerChunkForPopulation(properties.LimboDimensionID, chunkX, chunkZ);
        }

        return chunk;
    }

    @Override
    public void populate(int var2, int var3) {

    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    private double[] initializeNoiseField(double[] par1ArrayOfDouble, int par2, int par3, int par4, int par5, int par6, int par7) {
        if (par1ArrayOfDouble == null) {
            par1ArrayOfDouble = new double[par5 * par6 * par7];
        }

        if (this.parabolicField == null) {
            this.parabolicField = new float[25];

            for (int var8 = -2; var8 <= 2; ++var8) {
                for (int var9 = -2; var9 <= 2; ++var9) {
                    float var10 = 10.0F / MathHelper.sqrt(var8 * var8 + var9 * var9 + 0.2F);
                    this.parabolicField[var8 + 2 + (var9 + 2) * 5] = var10;
                }
            }
        }

        double var44 = 884.412D; //large values here create spiky land. add a 0, good -default 884
        double var45 = 9840.412D; //large values here make sheets- default - 684
        this.noise5 = this.noiseGen5.generateNoiseOctaves(this.noise5, par2, par4, par5, par7, 1.121D, 1.121D, 0.5D);
        this.noise6 = this.noiseGen6.generateNoiseOctaves(this.noise6, par2, par4, par5, par7, 200.0D, 200.0D, 0.5D);
        this.noise3 = this.noiseGen3.generateNoiseOctaves(this.noise3, par2, par3, par4, par5, par6, par7, var44 / 80.0D, var45 / 160.0D, var44 / 80.0D);
        this.noise1 = this.noiseGen1.generateNoiseOctaves(this.noise1, par2, par3, par4, par5, par6, par7, var44, var45, var44);
        this.noise2 = this.noiseGen2.generateNoiseOctaves(this.noise2, par2, par3, par4, par5, par6, par7, var44, var45, var44);

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
                        float var24 = this.parabolicField[var21 + 2 + (var22 + 2) * 5] / (Biomes.PLAINS.getBaseHeight() + 9.0F);


                        //this adjusts the height of the terrain

                        var16 += Biomes.PLAINS.getHeightVariation() * var24+4;
                        var17 += Biomes.PLAINS.getBaseHeight() * var24-1;
                        var18 += var24;
                    }
                }

                var16 /= var18;
                var17 /= var18;
                var16 =  (var16 * 0.9F + 0.1F);
                var17 = (var17 * 4.0F - 1.0F) / 8.0F;
                double var47 = this.noise6[var13] / 8000.0D;

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
                }
                else {
                    if (var47 > 1.0D) {
                        var47 = 1.0D;
                    }

                    var47 /= 8.0D;
                }

                ++var13;

                for (int var46 = 0; var46 < par6; ++var46) {
                    double var48 = var17;
                    double var26 = var16;
                    var48 += var47 * 0.2D;
                    var48 = var48 * par6 / 16.0D;
                    double var28 = par6 / 2.0D + var48 * 4.0D;
                    double var30 = 0.0D;
                    double var32 = (var46 - var28) * 12.0D * 128.0D / 128.0D / var26;

                    if (var32 < 0.0D) {
                        var32 *= 4.0D;
                    }

                    double var34 = this.noise1[var12] / 512.0D;
                    double var36 = this.noise2[var12] / 512.0D;
                    double var38 = (this.noise3[var12] / 10.0D + 1.0D) / 2.0D;

                    if (var38 < 0.0D) {
                        var30 = var34;
                    }
                    else if (var38 > 1.0D) {
                        var30 = var36;
                    }
                    else {
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

    public void scale(int x, int z, ChunkPrimer primer) { //Coursty of
        // TODO: this:
        this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10);
        this.noiseArray = this.initializeNoiseField(this.noiseArray, x * 4, 0, z * 4, 5, 17, 5);

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
                    double v0y0 = this.noiseArray[i0_0 + sectionY];
                    double v0y1 = this.noiseArray[i0_1 + sectionY];
                    double v1y0 = this.noiseArray[i1_0 + sectionY];
                    double v1y1 = this.noiseArray[i1_1 + sectionY];
                    double d0y0 = (this.noiseArray[i0_0 + sectionY + 1] - v0y0) * yScale;
                    double d0y1 = (this.noiseArray[i0_1 + sectionY + 1] - v0y1) * yScale;
                    double d1y0 = (this.noiseArray[i1_0 + sectionY + 1] - v1y0) * yScale;
                    double d1y1 = (this.noiseArray[i1_1 + sectionY + 1] - v1y1) * yScale;

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
                                if(vxyz > 0) {
                                    primer.setBlockState(xCoord, yCoord, zCoord, ModBlocks.blockLimbo.getDefaultState());
                                } else if(yCoord < 6) {
                                    primer.setBlockState(xCoord, yCoord, zCoord, ModBlocks.blockDimWall.getDefaultState().withProperty(BlockDimWall.TYPE, BlockDimWall.EnumType.ANCIENT));
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
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType par1EnumCreatureType, BlockPos pos) {
        return new ArrayList<Biome.SpawnListEntry>();
    }

    @Nullable
    @Override
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {

    }
}