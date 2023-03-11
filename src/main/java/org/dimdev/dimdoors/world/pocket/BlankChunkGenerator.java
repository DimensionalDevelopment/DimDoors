package org.dimdev.dimdoors.world.pocket;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class BlankChunkGenerator extends ChunkGenerator {
	public static final Codec<BlankChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
					instance.group(BiomeSource.CODEC.fieldOf("biome_source")
							.forGetter((generator) -> generator.biomeSource)
			).apply(instance, instance.stable(BlankChunkGenerator::of))
	);

	private static BlankChunkGenerator of(BiomeSource biomeSource) {
		return new BlankChunkGenerator(biomeSource);
	}

	private BlankChunkGenerator(BiomeSource biomeSource) {
		super(biomeSource);
	}

	@Override
	public Codec<? extends ChunkGenerator> codec() {
		return CODEC;
	}

//	@Override
//	public ChunkGenerator withSeed(long seed) {
//		return this;
//	}
//
//	@Override
//	public MultiNoiseUtil.MultiNoiseSampler getMultiNoiseSampler() {
//		return null;
//	}

	@Override
	public void applyCarvers(WorldGenRegion chunkRegion, long seed, RandomState noiseConfig, BiomeManager world, StructureManager structureAccessor, ChunkAccess chunk, GenerationStep.Carving carverStep) {
	}

	@Override
	public void buildSurface(WorldGenRegion region, StructureManager structures, RandomState noiseConfig, ChunkAccess chunk) {
	}

	@Override
	public void spawnOriginalMobs(WorldGenRegion region) {

	}

	@Override
	public int getGenDepth() {
		return 0;
	}

	@Override
	public void createReferences(WorldGenLevel structureWorldAccess, StructureManager accessor, ChunkAccess chunk) {
	}

	@Override
	public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState noiseConfig, StructureManager structureAccessor, ChunkAccess chunk) {
		return CompletableFuture.supplyAsync(() -> chunk);
	}

	@Override
	public int getSeaLevel() {
		return 0;
	}

	@Override
	public int getMinY() {
		return 0;
	}

	@Override
	public int getBaseHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor world, RandomState noiseConfig) {
		return 0;
	}

	@Override
	public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor world, RandomState noiseConfig) {
		return new NoiseColumn(0, new BlockState[0]);
	}

	@Override
	public void addDebugScreenInfo(List<String> text, RandomState noiseConfig, BlockPos pos) {
	}
}
