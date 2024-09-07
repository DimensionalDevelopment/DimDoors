package org.dimdev.dimdoors.pockets.generator;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ChunkGenerator extends PocketGenerator {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "chunk";

	private ResourceLocation dimensionID;
	private Vec3i size; // TODO: equation-ify
	private int virtualYOffset; // TODO: equation-ify

	public ChunkGenerator() {
	}

	@Override
	public PocketGenerator fromNbt(CompoundTag nbt, ResourceManager manager) {
		super.fromNbt(nbt, manager);

		this.dimensionID = new ResourceLocation(nbt.getString("dimension_id"));

		int[] temp = nbt.getIntArray("size");
		this.size = new Vec3i(temp[0], temp[1], temp[2]);

		this.virtualYOffset = nbt.contains("virtual_y_offset") ? nbt.getInt("virtual_y_offset") : 0;
		return this;
	}

	@Override
	public CompoundTag toNbtInternal(CompoundTag nbt, boolean allowReference) {
		super.toNbtInternal(nbt, allowReference);

		nbt.putString("dimension_id", dimensionID.toString());
		nbt.putIntArray("size", new int[]{this.size.getX(), this.size.getY(), this.size.getZ()});
		nbt.putInt("virtual_y_offset", this.virtualYOffset);
		return nbt;
	}

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
		ServerLevel world = parameters.world();
		VirtualLocation sourceVirtualLocation = parameters.sourceVirtualLocation();

		int chunkSizeX = ((this.size.getX() >> 4) + (this.size.getX() % 16 == 0 ? 0 : 1));
		int chunkSizeZ = ((this.size.getZ() >> 4) + (this.size.getZ() % 16 == 0 ? 0 : 1));

		Pocket pocket = DimensionalRegistry.getPocketDirectory(world.dimension()).newPocket(builder);

		LOGGER.info("Generating chunk pocket at location " + pocket.getOrigin());

		ServerLevel genWorld = DimensionalDoors.getWorld(ResourceKey.create(Registry.DIMENSION_REGISTRY, dimensionID));
		net.minecraft.world.level.chunk.ChunkGenerator genWorldChunkGenerator = genWorld.getChunkSource().getGenerator();

		RandomState config = RandomState.create(NoiseGeneratorSettings.dummy(), world.registryAccess().registryOrThrow(Registry.NOISE_REGISTRY), world.getSeed());

		ArrayList<ChunkAccess> protoChunks = new ArrayList<>();
		for (int z = 0; z < chunkSizeZ; z++) {
			for (int x = 0; x < chunkSizeX; x++) {
				ProtoChunk protoChunk = new ProtoChunk(new ChunkPos(pocket.getOrigin().offset(x * 16, 0, z * 16)), UpgradeData.EMPTY, world, genWorld.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), null);
				protoChunk.setLightEngine(genWorld.getLightEngine());
				protoChunks.add(protoChunk);
			}
		}
		WorldGenRegion protoRegion = new ChunkRegionHack(genWorld, protoChunks);//TODO Redo?
		for (ChunkAccess protoChunk : protoChunks) { // TODO: check wether structures are even activated
			genWorldChunkGenerator.createStructures(genWorld.registryAccess(), genWorld.getChunkSource().randomState(), genWorld.structureManager(), protoChunk, genWorld.getStructureManager(), genWorld.getSeed());
			((ProtoChunk) protoChunk).setStatus(ChunkStatus.STRUCTURE_STARTS);
		}
		for (ChunkAccess protoChunk : protoChunks) {
			genWorldChunkGenerator.createReferences(protoRegion, genWorld.structureManager().forWorldGenRegion(protoRegion), protoChunk);
			((ProtoChunk) protoChunk).setStatus(ChunkStatus.STRUCTURE_REFERENCES);
		}
		for (ChunkAccess protoChunk : protoChunks) {
			genWorldChunkGenerator.createBiomes(genWorld.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), Util.backgroundExecutor(), config, Blender.empty(), genWorld.structureManager(), protoChunk);
			((ProtoChunk) protoChunk).setStatus(ChunkStatus.BIOMES);
		}
		for (ChunkAccess protoChunk : protoChunks) {
			try {
				genWorldChunkGenerator.fillFromNoise(Util.backgroundExecutor(), Blender.empty(), config, genWorld.structureManager().forWorldGenRegion(protoRegion), protoChunk).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			((ProtoChunk) protoChunk).setStatus(ChunkStatus.NOISE);
		}
		for (ChunkAccess protoChunk : protoChunks) {
			genWorldChunkGenerator.buildSurface(protoRegion, genWorld.structureManager(), config, protoChunk);
			((ProtoChunk) protoChunk).setStatus(ChunkStatus.SURFACE);
		}
		for (GenerationStep.Carving carver : GenerationStep.Carving.values()) {
			for (ChunkAccess protoChunk : protoChunks) {
				genWorldChunkGenerator.applyCarvers(protoRegion, genWorld.getSeed(), config, genWorld.getBiomeManager(), genWorld.structureManager(), protoChunk, carver);
				ProtoChunk pChunk = ((ProtoChunk) protoChunk);
				if (pChunk.getStatus() == ChunkStatus.SURFACE) pChunk.setStatus(ChunkStatus.CARVERS);
				else pChunk.setStatus(ChunkStatus.CARVERS);
			}
		}
		for (ChunkAccess protoChunk : protoChunks) {
			WorldGenRegion tempRegion = new ChunkRegionHack(genWorld, ChunkPos.rangeClosed(protoChunk.getPos(), 10).map(chunkPos -> protoRegion.getChunk(chunkPos.x, chunkPos.z)).collect(Collectors.toList()));
			genWorldChunkGenerator.applyBiomeDecoration(tempRegion, protoChunk, genWorld.structureManager().forWorldGenRegion(tempRegion));
			((ProtoChunk) protoChunk).setStatus(ChunkStatus.FEATURES);
		}
		for (ChunkAccess protoChunk : protoChunks) { // likely only necessary for spawn step since we copy over anyways
			((ThreadedLevelLightEngine) genWorld.getLightEngine()).lightChunk(protoChunk, false);
			((ProtoChunk) protoChunk).setStatus(ChunkStatus.LIGHT);
		}
		for (ChunkAccess protoChunk : protoChunks) { // TODO: does this even work?
			WorldGenRegion tempRegion = new ChunkRegionHack(genWorld, ChunkPos.rangeClosed(protoChunk.getPos(), 5).map(chunkPos -> protoRegion.getChunk(chunkPos.x, chunkPos.z)).collect(Collectors.toList()));
			genWorldChunkGenerator.spawnOriginalMobs(tempRegion);
			((ProtoChunk) protoChunk).setStatus(ChunkStatus.SPAWN);
		}


		BlockPos firstCorner = pocket.getOrigin();
		BlockPos secondCorner = new BlockPos(firstCorner.getX() + size.getX() - 1, Math.min(firstCorner.getY() + size.getY() - 1, world.getHeight() - virtualYOffset - 1), firstCorner.getZ() + size.getZ() - 1); // subtracting 1 here since it should be 0 inclusive and size exclusive

		BlockPos pocketOriginChunkOffset = new ChunkPos(pocket.getOrigin()).getWorldPosition().subtract(firstCorner);
		for (BlockPos blockPos : BlockPos.betweenClosed(firstCorner, secondCorner)) {
			BlockPos sourcePos = blockPos.offset(pocketOriginChunkOffset).offset(0, virtualYOffset, 0);
			BlockState blockState = protoRegion.getBlockState(sourcePos);
			if (!blockState.isAir()) {
				world.setBlockAndUpdate(blockPos, protoRegion.getBlockState(blockPos.offset(pocketOriginChunkOffset).offset(0, virtualYOffset, 0)));
			}
		}
		AABB realBox = new AABB(firstCorner, secondCorner);
		for (ChunkAccess protoChunk : protoChunks) {
			for(BlockPos virtualBlockPos : protoChunk.getBlockEntitiesPos()) {
				BlockPos realBlockPos = virtualBlockPos.subtract(pocketOriginChunkOffset).offset(0, -virtualYOffset, 0);
				if (realBox.contains(realBlockPos.getX(), realBlockPos.getY(), realBlockPos.getZ())) {
					world.setBlockEntity(protoChunk.getBlockEntity(virtualBlockPos)); // TODO: ensure this works, likely bugged
				}
			}
		}
		AABB virtualBox = realBox.move(pocketOriginChunkOffset.offset(0, virtualYOffset, 0));
		/*
		for (Entity entity : protoRegion.getOtherEntities(null, virtualBox)) { // TODO: does this even work?
			TeleportUtil.teleport(entity, world, entity.getPos().add(-pocketOriginChunkOffset.getX(), -pocketOriginChunkOffset.getY() - virtualYOffset, -pocketOriginChunkOffset.getZ()), entity.yaw);
		} // TODO: Entities?/ Biomes/ Structure Data
		*/
		world.setBlockAndUpdate(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pocket.getOrigin()), ModBlocks.DETACHED_RIFT.get().defaultBlockState());

		DetachedRiftBlockEntity rift = ModBlockEntityTypes.DETACHED_RIFT.get().create(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pocket.getOrigin()), ModBlocks.DETACHED_RIFT.get().defaultBlockState());
		world.setBlockEntity(rift);

		rift.setDestination(new PocketEntranceMarker());
		pocket.virtualLocation = sourceVirtualLocation;

		return pocket;
	}

	@Override
	public PocketGeneratorType<? extends PocketGenerator> getType() {
		return PocketGeneratorType.CHUNK.get();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Vec3i getSize(PocketGenerationContext parameters) {
		return size;
	}

	private static class ChunkRegionHack extends WorldGenRegion { // Please someone tell me if there is a better way
		ChunkRegionHack(ServerLevel world, List<ChunkAccess> chunks) {
			super(world, chunks, ChunkStatus.EMPTY, 0);
		}

		@Override
		public ChunkAccess getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
			ChunkAccess chunk = super.getChunk(chunkX, chunkZ, leastStatus, false);
			return chunk == null ? new ProtoChunkHack(new ChunkPos(chunkX, chunkZ), UpgradeData.EMPTY, this, this.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY)) : chunk;
		}

		// TODO: Override getSeed()
	}

	private static class ProtoChunkHack extends ProtoChunk { // exists solely to make some calls in the non utilized chunks faster
		public ProtoChunkHack(ChunkPos pos, UpgradeData upgradeData, LevelHeightAccessor world, Registry<Biome> biomeRegistry) {
			super(pos, upgradeData, world, biomeRegistry, null);
		}

		@Override
		public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
			return Blocks.VOID_AIR.defaultBlockState();
		}

		@Override
		public BlockState getBlockState(BlockPos pos) {
			return Blocks.VOID_AIR.defaultBlockState();
		}

		@Override
		public FluidState getFluidState(BlockPos pos) {
			return Fluids.EMPTY.defaultFluidState();
		}
	}
}
