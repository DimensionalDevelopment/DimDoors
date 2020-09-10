package org.dimdev.dimdoors.world.limbo;

import java.util.Iterator;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import com.mojang.serialization.Codec;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.mixin.ChunkGeneratorAccessor;
import org.dimdev.dimdoors.world.ModDimensions;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.StructureFeature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class LimboChunkGenerator extends ChunkGenerator {
    public static final LimboChunkGenerator INSTANCE = new LimboChunkGenerator(LimboBiomeSource.INSTANCE, LimboBiomeSource.INSTANCE);
    public static final Codec<LimboChunkGenerator> CODEC = Codec.unit(INSTANCE);
    private static final float[] NOISE_WEIGHT_TABLE = Util.make(new float[13824], (array) -> {
        for (int i = 0; i < 24; ++i) {
            for (int j = 0; j < 24; ++j) {
                for (int k = 0; k < 24; ++k) {
                    array[i * 24 * 24 + j * 24 + k] = (float) calculateNoiseWeight(j - 12, k - 12, i - 12);
                }
            }
        }
    });
    private static final float[] BIOME_WEIGHT_TABLE = Util.make(new float[25], (fs) -> {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f = 10.0F / MathHelper.sqrt((float) (i * i + j * j) + 0.2F);
                fs[i + 2 + (j + 2) * 5] = f;
            }
        }
    });

    protected final ChunkRandom random;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;
    protected final Supplier<ChunkGeneratorSettings> settings;
    private final int verticalNoiseResolution;
    private final int horizontalNoiseResolution;
    private final int noiseSizeX;
    private final int noiseSizeY;
    private final int noiseSizeZ;
    private final OctavePerlinNoiseSampler lowerInterpolatedNoise;
    private final OctavePerlinNoiseSampler upperInterpolatedNoise;
    private final OctavePerlinNoiseSampler interpolationNoise;
    private final NoiseSampler surfaceDepthNoise;
    private final OctavePerlinNoiseSampler densityNoise;
    @Nullable
    private final SimplexNoiseSampler islandNoise;
    public final long worldSeed;
    private final int worldHeight;

    private LimboChunkGenerator(BiomeSource biomeSource, BiomeSource biomeSource2) {
        super(biomeSource, biomeSource2, ModDimensions.LIMBO_CHUNK_GENERATOR_SETTINGS.getStructuresConfig(), new Random().nextLong());
        this.worldSeed = ((ChunkGeneratorAccessor) this).getWorldSeed();
        ChunkGeneratorSettings chunkGeneratorSettings = ModDimensions.LIMBO_CHUNK_GENERATOR_SETTINGS;
        this.settings = () -> ModDimensions.LIMBO_CHUNK_GENERATOR_SETTINGS;
        GenerationShapeConfig generationShapeConfig = chunkGeneratorSettings.getGenerationShapeConfig();
        this.worldHeight = generationShapeConfig.getHeight();
        this.verticalNoiseResolution = generationShapeConfig.getSizeVertical() * 4;
        this.horizontalNoiseResolution = generationShapeConfig.getSizeHorizontal() * 4;
        this.defaultBlock = chunkGeneratorSettings.getDefaultBlock();
        this.defaultFluid = chunkGeneratorSettings.getDefaultFluid();
        this.noiseSizeX = 16 / this.horizontalNoiseResolution;
        this.noiseSizeY = generationShapeConfig.getHeight() / this.verticalNoiseResolution;
        this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
        this.random = new ChunkRandom(this.worldSeed);
        this.lowerInterpolatedNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
        this.upperInterpolatedNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
        this.interpolationNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-7, 0));
        this.surfaceDepthNoise = generationShapeConfig.hasSimplexSurfaceNoise() ? new OctaveSimplexNoiseSampler(this.random, IntStream.rangeClosed(-3, 0)) : new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-3, 0));
        this.random.consume(2620);
        this.densityNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
        if (generationShapeConfig.hasIslandNoiseOverride()) {
            ChunkRandom chunkRandom = new ChunkRandom(worldSeed);
            chunkRandom.consume(17292);
            this.islandNoise = new SimplexNoiseSampler(chunkRandom);
        } else {
            this.islandNoise = null;
        }
    }

    private static double getNoiseWeight(int x, int y, int z) {
        int i = x + 12;
        int j = y + 12;
        int k = z + 12;
        if (i >= 0 && i < 24) {
            if (j >= 0 && j < 24) {
                return k >= 0 && k < 24 ? (double) NOISE_WEIGHT_TABLE[k * 24 * 24 + i * 24 + j] : 0.0D;
            } else {
                return 0.0D;
            }
        } else {
            return 0.0D;
        }
    }

    private static double calculateNoiseWeight(int x, int y, int z) {
        double d = x * x + z * z;
        double e = (double) y + 0.5D;
        double f = e * e;
        double g = Math.pow(2.718281828459045D, -(f / 16.0D + d / 16.0D));
        double h = -e * MathHelper.fastInverseSqrt(f / 2.0D + d / 2.0D) / 2.0D;
        return h * g;
    }

    @Override
    public Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Environment(EnvType.CLIENT)
    public ChunkGenerator withSeed(long seed) {
        BiomeSource source = this.biomeSource.withSeed(seed);
        return new LimboChunkGenerator(source, source);
    }

    private double sampleNoise(int x, int y, int z, double horizontalScale, double verticalScale, double horizontalStretch, double verticalStretch) {
        double d = 0.0D;
        double e = 0.0D;
        double f = 0.0D;
        double g = 1.0D;
        for (int i = 0; i < 16; ++i) {
            double h = OctavePerlinNoiseSampler.maintainPrecision((double) x * horizontalScale * g);
            double j = OctavePerlinNoiseSampler.maintainPrecision((double) y * verticalScale * g);
            double k = OctavePerlinNoiseSampler.maintainPrecision((double) z * horizontalScale * g);
            double l = verticalScale * g;
            PerlinNoiseSampler perlinNoiseSampler = this.lowerInterpolatedNoise.getOctave(i);
            if (perlinNoiseSampler != null) {
                d += perlinNoiseSampler.sample(h, j, k, l, (double) y * l) / g;
            }
            PerlinNoiseSampler perlinNoiseSampler2 = this.upperInterpolatedNoise.getOctave(i);
            if (perlinNoiseSampler2 != null) {
                e += perlinNoiseSampler2.sample(h, j, k, l, (double) y * l) / g;
            }
            if (i < 8) {
                PerlinNoiseSampler perlinNoiseSampler3 = this.interpolationNoise.getOctave(i);
                if (perlinNoiseSampler3 != null) {
                    f += perlinNoiseSampler3.sample(OctavePerlinNoiseSampler.maintainPrecision((double) x * horizontalStretch * g), OctavePerlinNoiseSampler.maintainPrecision((double) y * verticalStretch * g), OctavePerlinNoiseSampler.maintainPrecision((double) z * horizontalStretch * g), verticalStretch * g, (double) y * verticalStretch * g) / g;
                }
            }
            g /= 2.0D;
        }
        return MathHelper.clampedLerp(d / 512.0D, e / 512.0D, (f / 10.0D + 1.0D) / 2.0D);
    }

    private double[] sampleNoiseColumn(int x, int z) {
        double[] ds = new double[this.noiseSizeY + 1];
        this.sampleNoiseColumn(ds, x, z);
        return ds;
    }

    private void sampleNoiseColumn(double[] buffer, int x, int z) {
        GenerationShapeConfig generationShapeConfig = this.settings.get().getGenerationShapeConfig();
        double ac;
        double ad;
        double topSlideTarget;
        double topSlideSize;
        if (this.islandNoise != null) {
            ac = TheEndBiomeSource.getNoiseAt(this.islandNoise, x, z) - 8.0F;
            if (ac > 0.0D) {
                ad = 0.25D;
            } else {
                ad = 1.0D;
            }
        } else {
            float g = 0.0F;
            float h = 0.0F;
            float i = 0.0F;
            int k = this.getSeaLevel();
            float l = this.biomeSource.getBiomeForNoiseGen(x, k, z).getDepth();
            for (int m = -2; m <= 2; ++m) {
                for (int n = -2; n <= 2; ++n) {
                    Biome biome = this.biomeSource.getBiomeForNoiseGen(x + m, k, z + n);
                    float o = biome.getDepth();
                    float p = biome.getScale();
                    float s;
                    float t;
                    if (generationShapeConfig.isAmplified() && o > 0.0F) {
                        s = 1.0F + o * 2.0F;
                        t = 1.0F + p * 4.0F;
                    } else {
                        s = o;
                        t = p;
                    }
                    float u = o > l ? 0.5F : 1.0F;
                    float v = u * BIOME_WEIGHT_TABLE[m + 2 + (n + 2) * 5] / (s + 2.0F);
                    g += t * v;
                    h += s * v;
                    i += v;
                }
            }
            float w = h / i;
            float y = g / i;
            topSlideTarget = w * 0.5F - 0.125F;
            topSlideSize = y * 0.9F + 0.1F;
            ac = topSlideTarget * 0.265625D;
            ad = 96.0D / topSlideSize;
        }
        double xzScale = 684.412D * generationShapeConfig.getSampling().getXZScale();
        double yScale = 684.412D * generationShapeConfig.getSampling().getYScale();
        double xzFactor = xzScale / generationShapeConfig.getSampling().getXZFactor();
        double yFactor = yScale / generationShapeConfig.getSampling().getYFactor();
        topSlideTarget = generationShapeConfig.getTopSlide().getTarget();
        topSlideSize = generationShapeConfig.getTopSlide().getSize();
        double topSlideOffset = generationShapeConfig.getTopSlide().getOffset();
        double bottomSlideTarget = generationShapeConfig.getBottomSlide().getTarget();
        double bottomSlideSize = generationShapeConfig.getBottomSlide().getSize();
        double bottomSlideOffset = generationShapeConfig.getBottomSlide().getOffset();
        double randomDensity = generationShapeConfig.hasRandomDensityOffset() ? this.getRandomDensityAt(x, z) : 0.0D;
        double densityFactor = generationShapeConfig.getDensityFactor();
        double densityOffset = generationShapeConfig.getDensityOffset();

        for (int loop = 0; loop <= this.noiseSizeY; ++loop) {
            double as = this.sampleNoise(x, loop, z, xzScale, yScale, xzFactor, yFactor);
            double at = 1.0D - (double) loop * 2.0D / (double) this.noiseSizeY + randomDensity;
            double au = at * densityFactor + densityOffset;
            double av = (au + ac) * ad;
            if (av > 0.0D) {
                as += av * 4.0D;
            } else {
                as += av;
            }

            double ax;
            if (topSlideSize > 0.0D) {
                ax = ((double) (this.noiseSizeY - loop) - topSlideOffset) / topSlideSize;
                as = MathHelper.clampedLerp(topSlideTarget, as, ax);
            }

            if (bottomSlideSize > 0.0D) {
                ax = ((double) loop - bottomSlideOffset) / bottomSlideSize;
                as = MathHelper.clampedLerp(bottomSlideTarget, as, ax);
            }

            buffer[loop] = as;
        }

    }

    private double getRandomDensityAt(int x, int z) {
        double d = this.densityNoise.sample(x * 200, 10.0D, z * 200, 1.0D, 0.0D, true);
        double f;
        if (d < 0.0D) {
            f = -d * 0.3D;
        } else {
            f = d;
        }

        double g = f * 24.575625D - 2.0D;
        return g < 0.0D ? g * 0.009486607142857142D : Math.min(g, 1.0D) * 0.006640625D;
    }

    public int getHeight(int x, int z, Heightmap.Type heightmapType) {
        return this.sampleHeightmap(x, z, null, heightmapType.getBlockPredicate());
    }

    public BlockView getColumnSample(int x, int z) {
        BlockState[] blockStates = new BlockState[this.noiseSizeY * this.verticalNoiseResolution];
        this.sampleHeightmap(x, z, blockStates, null);
        return new VerticalBlockSample(blockStates);
    }

    private int sampleHeightmap(int x, int z, BlockState[] states, Predicate<BlockState> predicate) {
        int i = Math.floorDiv(x, this.horizontalNoiseResolution);
        int j = Math.floorDiv(z, this.horizontalNoiseResolution);
        int k = Math.floorMod(x, this.horizontalNoiseResolution);
        int l = Math.floorMod(z, this.horizontalNoiseResolution);
        double d = (double) k / (double) this.horizontalNoiseResolution;
        double e = (double) l / (double) this.horizontalNoiseResolution;
        double[][] ds = new double[][]{this.sampleNoiseColumn(i, j), this.sampleNoiseColumn(i, j + 1), this.sampleNoiseColumn(i + 1, j), this.sampleNoiseColumn(i + 1, j + 1)};

        for (int m = this.noiseSizeY - 1; m >= 0; --m) {
            double f = ds[0][m];
            double g = ds[1][m];
            double h = ds[2][m];
            double n = ds[3][m];
            double o = ds[0][m + 1];
            double p = ds[1][m + 1];
            double q = ds[2][m + 1];
            double r = ds[3][m + 1];

            for (int s = this.verticalNoiseResolution - 1; s >= 0; --s) {
                double t = (double) s / (double) this.verticalNoiseResolution;
                double u = MathHelper.lerp3(t, d, e, f, o, h, q, g, p, n, r);
                int v = m * this.verticalNoiseResolution + s;
                BlockState blockState = this.getBlockState(u, v);
                if (states != null) {
                    states[v] = blockState;
                }

                if (predicate != null && predicate.test(blockState)) {
                    return v + 1;
                }
            }
        }

        return 0;
    }

    protected BlockState getBlockState(double density, int y) {
        BlockState blockState3;
        if (density > 0.0D) {
            blockState3 = this.defaultBlock;
        } else if (y < this.getSeaLevel()) {
            blockState3 = this.defaultFluid;
        } else {
            blockState3 = Blocks.AIR.getDefaultState();
        }

        return blockState3;
    }

    public void buildSurface(ChunkRegion region, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.x;
        int j = chunkPos.z;
        ChunkRandom chunkRandom = new ChunkRandom();
        chunkRandom.setTerrainSeed(i, j);
        ChunkPos chunkPos2 = chunk.getPos();
        int k = chunkPos2.getStartX();
        int l = chunkPos2.getStartZ();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int m = 0; m < 16; ++m) {
            for (int n = 0; n < 16; ++n) {
                int o = k + m;
                int p = l + n;
                int q = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, m, n) + 1;
                double e = this.surfaceDepthNoise.sample((double) o * 0.0625D, (double) p * 0.0625D, 0.0625D, (double) m * 0.0625D) * 15.0D;
                region.getBiome(mutable.set(k + m, q, l + n)).buildSurface(chunkRandom, chunk, o, p, q, e, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), region.getSeed());
            }
        }

        this.buildFluid(chunk, chunkRandom);
    }

    private void buildFluid(Chunk chunk, Random random) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int i = chunk.getPos().getStartX();
        int j = chunk.getPos().getStartZ();
        ChunkGeneratorSettings chunkGeneratorSettings = this.settings.get();
        int k = chunkGeneratorSettings.getBedrockFloorY();
        boolean bl2 = k + 4 >= 0 && k < this.worldHeight;
        if (bl2) {
            Iterator<BlockPos> iterator = BlockPos.iterate(i, 0, j, i + 15, 0, j + 15).iterator();
            while (true) {
                BlockPos blockPos;
                int o;
                if (!iterator.hasNext()) {
                    return;
                }

                blockPos = iterator.next();
                for (o = 4; o >= 0; --o) {
                    if (o <= random.nextInt(5)) {
                        chunk.setBlockState(mutable.set(blockPos.getX(), k + o, blockPos.getZ()), ModBlocks.ETERNAL_FLUID.getDefaultState(), false);
                    }
                }
            }
        }
    }

    public void populateNoise(WorldAccess world, StructureAccessor accessor, Chunk chunk) {
        ObjectList<StructurePiece> objectList = new ObjectArrayList<>(10);
        ObjectList<JigsawJunction> objectList2 = new ObjectArrayList<>(32);
        ChunkPos chunkPos = chunk.getPos();
        int posX = chunkPos.x;
        int posZ = chunkPos.z;
        int k = posX << 4;
        int l = posZ << 4;

        for (StructureFeature<?> feature : StructureFeature.JIGSAW_STRUCTURES) {
            accessor.getStructuresWithChildren(ChunkSectionPos.from(chunkPos, 0), feature).forEach((start) -> {
                Iterator<StructurePiece> var6 = start.getChildren().iterator();
                while (true) {
                    StructurePiece structurePiece;
                    do {
                        if (!var6.hasNext()) {
                            return;
                        }

                        structurePiece = var6.next();
                    } while (!structurePiece.intersectsChunk(chunkPos, 12));

                    if (structurePiece instanceof PoolStructurePiece) {
                        PoolStructurePiece poolStructurePiece = (PoolStructurePiece) structurePiece;
                        StructurePool.Projection projection = poolStructurePiece.getPoolElement().getProjection();
                        if (projection == StructurePool.Projection.RIGID) {
                            objectList.add(poolStructurePiece);
                        }

                        for (JigsawJunction jigsawJunction : poolStructurePiece.getJunctions()) {
                            int kx = jigsawJunction.getSourceX();
                            int lx = jigsawJunction.getSourceZ();
                            if (kx > k - 12 && lx > l - 12 && kx < k + 15 + 12 && lx < l + 15 + 12) {
                                objectList2.add(jigsawJunction);
                            }
                        }
                    } else {
                        objectList.add(structurePiece);
                    }
                }
            });
        }

        double[][][] noise = new double[2][this.noiseSizeZ + 1][this.noiseSizeY + 1];

        for (int m = 0; m < this.noiseSizeZ + 1; ++m) {
            noise[0][m] = new double[this.noiseSizeY + 1];
            this.sampleNoiseColumn(noise[0][m], posX * this.noiseSizeX, posZ * this.noiseSizeZ + m);
            noise[1][m] = new double[this.noiseSizeY + 1];
        }

        ProtoChunk protoChunk = (ProtoChunk) chunk;
        Heightmap heightmap = protoChunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmap2 = protoChunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        ObjectListIterator<StructurePiece> objectListIterator = objectList.iterator();
        ObjectListIterator<JigsawJunction> objectListIterator2 = objectList2.iterator();

        for (int n = 0; n < this.noiseSizeX; ++n) {
            int p;
            for (p = 0; p < this.noiseSizeZ + 1; ++p) {
                this.sampleNoiseColumn(noise[1][p], posX * this.noiseSizeX + n + 1, posZ * this.noiseSizeZ + p);
            }

            for (p = 0; p < this.noiseSizeZ; ++p) {
                ChunkSection chunkSection = protoChunk.getSection(15);
                chunkSection.lock();

                for (int q = this.noiseSizeY - 1; q >= 0; --q) {
                    double d = noise[0][p][q];
                    double e = noise[0][p + 1][q];
                    double f = noise[1][p][q];
                    double g = noise[1][p + 1][q];
                    double h = noise[0][p][q + 1];
                    double r = noise[0][p + 1][q + 1];
                    double s = noise[1][p][q + 1];
                    double t = noise[1][p + 1][q + 1];

                    for (int u = this.verticalNoiseResolution - 1; u >= 0; --u) {
                        int v = q * this.verticalNoiseResolution + u;
                        int w = v & 15;
                        int ax = v >> 4;
                        if (chunkSection.getYOffset() >> 4 != ax) {
                            chunkSection.unlock();
                            chunkSection = protoChunk.getSection(ax);
                            chunkSection.lock();
                        }

                        double y = (double) u / (double) this.verticalNoiseResolution;
                        double z = MathHelper.lerp(y, d, h);
                        double aa = MathHelper.lerp(y, f, s);
                        double ab = MathHelper.lerp(y, e, r);
                        double ac = MathHelper.lerp(y, g, t);

                        for (int ad = 0; ad < this.horizontalNoiseResolution; ++ad) {
                            int ae = k + n * this.horizontalNoiseResolution + ad;
                            int af = ae & 15;
                            double ag = (double) ad / (double) this.horizontalNoiseResolution;
                            double ah = MathHelper.lerp(ag, z, aa);
                            double ai = MathHelper.lerp(ag, ab, ac);

                            for (int aj = 0; aj < this.horizontalNoiseResolution; ++aj) {
                                int ak = l + p * this.horizontalNoiseResolution + aj;
                                int al = ak & 15;
                                double am = (double) aj / (double) this.horizontalNoiseResolution;
                                double an = MathHelper.lerp(am, ah, ai);
                                double ao = MathHelper.clamp(an / 200.0D, -1.0D, 1.0D);

                                int at;
                                int au;
                                int ar;
                                for (ao = ao / 2.0D - ao * ao * ao / 24.0D; objectListIterator.hasNext(); ao += getNoiseWeight(at, au, ar) * 0.8D) {
                                    StructurePiece structurePiece = objectListIterator.next();
                                    BlockBox blockBox = structurePiece.getBoundingBox();
                                    at = Math.max(0, Math.max(blockBox.minX - ae, ae - blockBox.maxX));
                                    au = v - (blockBox.minY + (structurePiece instanceof PoolStructurePiece ? ((PoolStructurePiece) structurePiece).getGroundLevelDelta() : 0));
                                    ar = Math.max(0, Math.max(blockBox.minZ - ak, ak - blockBox.maxZ));
                                }

                                objectListIterator.back(objectList.size());

                                while (objectListIterator2.hasNext()) {
                                    JigsawJunction jigsawJunction = objectListIterator2.next();
                                    int as = ae - jigsawJunction.getSourceX();
                                    at = v - jigsawJunction.getSourceGroundY();
                                    au = ak - jigsawJunction.getSourceZ();
                                    ao += getNoiseWeight(as, at, au) * 0.4D;
                                }

                                objectListIterator2.back(objectList2.size());
                                BlockState blockState = this.getBlockState(ao, v);
                                if (blockState != Blocks.AIR.getDefaultState()) {
                                    if (blockState.getLuminance() != 0) {
                                        mutable.set(ae, v, ak);
                                        protoChunk.addLightSource(mutable);
                                    }

                                    chunkSection.setBlockState(af, w, al, blockState, false);
                                    heightmap.trackUpdate(af, v, al, blockState);
                                    heightmap2.trackUpdate(af, v, al, blockState);
                                }
                            }
                        }
                    }
                }

                chunkSection.unlock();
            }

            double[][] es = noise[0];
            noise[0] = noise[1];
            noise[1] = es;
        }

    }

    public int getMaxY() {
        return this.worldHeight;
    }

    public int getSeaLevel() {
        return this.settings.get().getSeaLevel();
    }

    @Override
    public void populateEntities(ChunkRegion region) {
        int i = region.getCenterChunkX();
        int j = region.getCenterChunkZ();
        Biome biome = region.getBiome((new ChunkPos(i, j)).getCenterBlockPos());
        ChunkRandom chunkRandom = new ChunkRandom();
        chunkRandom.setPopulationSeed(region.getSeed(), i << 4, j << 4);
        SpawnHelper.populateEntities(region, biome, i, j, chunkRandom);
    }
}
