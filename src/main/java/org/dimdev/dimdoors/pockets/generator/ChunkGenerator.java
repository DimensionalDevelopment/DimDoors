package org.dimdev.dimdoors.pockets.generator;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.GenerationStep;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.world.level.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ChunkGenerator extends PocketGenerator {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "chunk";

	private Identifier dimensionID;
	private Vec3i size; // TODO: equation-ify
	private Vec3i offset; // TODO: equation-ify
	private int virtualYOffset; // TODO: equation-ify

	public ChunkGenerator() {
	}

	@Override
	public PocketGenerator fromTag(CompoundTag tag) {
		super.fromTag(tag);

		this.dimensionID = new Identifier(tag.getString("dimension_id"));

		int[] temp = tag.getIntArray("size");
		this.size = new Vec3i(temp[0], temp[1], temp[2]);

		temp = tag.contains("offset") ? tag.getIntArray("offset") : new int[]{0, 0, 0};
		this.offset = new Vec3i(temp[0], temp[1], temp[2]);

		this.virtualYOffset = tag.contains("virtual_y_offset") ? tag.getInt("virtual_y_offset") : 0;
		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		tag.putString("dimension_id", dimensionID.toString());
		tag.putIntArray("size", new int[]{this.size.getX(), this.size.getY(), this.size.getZ()});
		if (!(offset.getX() == 0 && offset.getY() == 0 && offset.getZ() == 0)) {
			tag.putIntArray("offset", new int[]{this.offset.getX(), this.offset.getY(), this.offset.getZ()});
		}
		tag.putInt("virtual_y_offset", this.virtualYOffset);
		return tag;
	}

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationParameters parameters, Pocket.PocketBuilder<?, ?> builder) {
		ServerWorld world = parameters.getWorld();
		VirtualLocation sourceVirtualLocation = parameters.getSourceVirtualLocation();

		int chunkSizeX = ((this.size.getX() >> 4) + (this.size.getX() % 16 == 0 ? 0 : 1));
		int chunkSizeZ = ((this.size.getZ() >> 4) + (this.size.getZ() % 16 == 0 ? 0 : 1));

		Pocket pocket = DimensionalRegistry.getPocketDirectory(world.getRegistryKey()).newPocket(builder);
		pocket.setSize(size);
		pocket.offsetOrigin(offset);

		LOGGER.info("Generating chunk pocket at location " + pocket.getOrigin());

		ServerWorld genWorld = DimensionalDoorsInitializer.getWorld(RegistryKey.of(Registry.DIMENSION, dimensionID));
		net.minecraft.world.gen.chunk.ChunkGenerator genWorldChunkGenerator = genWorld.getChunkManager().getChunkGenerator();

		ArrayList<Chunk> protoChunks = new ArrayList<>();
		for (int z = 0; z < chunkSizeZ; z++) {
			for (int x = 0; x < chunkSizeX; x++) {
				ProtoChunk protoChunk = new ProtoChunk(new ChunkPos(pocket.getOrigin().add(x * 16, 0, z * 16)), UpgradeData.NO_UPGRADE_DATA, world);
				protoChunk.setLightingProvider(genWorld.getLightingProvider());
				protoChunks.add(protoChunk);
			}
		}
		ChunkRegion protoRegion = new ChunkRegionHack(genWorld, protoChunks);
		for (Chunk protoChunk : protoChunks) { // TODO: check wether structures are even activated
			genWorldChunkGenerator.setStructureStarts(genWorld.getRegistryManager(), genWorld.getStructureAccessor().forRegion(protoRegion), protoChunk, genWorld.getStructureManager(), genWorld.getSeed());
			((ProtoChunk) protoChunk).setStatus(ChunkStatus.STRUCTURE_STARTS);
		}
		for (Chunk protoChunk : protoChunks) {
			genWorldChunkGenerator.addStructureReferences(protoRegion, genWorld.getStructureAccessor().forRegion(protoRegion), protoChunk);
			((ProtoChunk) protoChunk).setStatus(ChunkStatus.STRUCTURE_REFERENCES);
		}
		for (Chunk protoChunk : protoChunks) {
			genWorldChunkGenerator.populateBiomes(BuiltinRegistries.BIOME, protoChunk);
			((ProtoChunk) protoChunk).setStatus(ChunkStatus.BIOMES);
		}
		for (Chunk protoChunk : protoChunks) {
			try {
				genWorldChunkGenerator.populateNoise(Util.getMainWorkerExecutor(), genWorld.getStructureAccessor().forRegion(protoRegion), protoChunk).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			((ProtoChunk) protoChunk).setStatus(ChunkStatus.NOISE);
		}
		for (Chunk protoChunk : protoChunks) {
			genWorldChunkGenerator.buildSurface(protoRegion, protoChunk);
			((ProtoChunk) protoChunk).setStatus(ChunkStatus.SURFACE);
		}
		for (GenerationStep.Carver carver : GenerationStep.Carver.values()) {
			for (Chunk protoChunk : protoChunks) {
				genWorldChunkGenerator.carve(genWorld.getSeed(), genWorld.getBiomeAccess(), protoChunk, carver);
				ProtoChunk pChunk = ((ProtoChunk) protoChunk);
				if (pChunk.getStatus() == ChunkStatus.SURFACE) pChunk.setStatus(ChunkStatus.CARVERS);
				else pChunk.setStatus(ChunkStatus.LIQUID_CARVERS);
			}
		}
		for (Chunk protoChunk : protoChunks) {
			ChunkRegion tempRegion = new ChunkRegionHack(genWorld, ChunkPos.stream(protoChunk.getPos(), 10).map(chunkPos -> protoRegion.getChunk(chunkPos.x, chunkPos.z)).collect(Collectors.toList()));
			genWorldChunkGenerator.generateFeatures(tempRegion, genWorld.getStructureAccessor().forRegion(tempRegion));
			((ProtoChunk) protoChunk).setStatus(ChunkStatus.FEATURES);
		}
		for (Chunk protoChunk : protoChunks) { // likely only necessary for spawn step since we copy over anyways
			((ServerLightingProvider) genWorld.getLightingProvider()).light(protoChunk, false);
			((ProtoChunk) protoChunk).setStatus(ChunkStatus.LIGHT);
		}
		for (Chunk protoChunk : protoChunks) { // TODO: does this even work?
			ChunkRegion tempRegion = new ChunkRegionHack(genWorld, ChunkPos.stream(protoChunk.getPos(), 5).map(chunkPos -> protoRegion.getChunk(chunkPos.x, chunkPos.z)).collect(Collectors.toList()));
			genWorldChunkGenerator.populateEntities(tempRegion);
			((ProtoChunk) protoChunk).setStatus(ChunkStatus.SPAWN);
		}


		BlockPos firstCorner = pocket.getOrigin();
		BlockPos secondCorner = new BlockPos(firstCorner.getX() + size.getX() - 1, Math.min(firstCorner.getY() + size.getY() - 1, world.getHeight() - virtualYOffset - 1), firstCorner.getZ() + size.getZ() - 1); // subtracting 1 here since it should be 0 inclusive and size exclusive

		BlockPos pocketOriginChunkOffset = new ChunkPos(pocket.getOrigin()).getStartPos().subtract(firstCorner);
		for (BlockPos blockPos : BlockPos.iterate(firstCorner, secondCorner)) {
			BlockPos sourcePos = blockPos.add(pocketOriginChunkOffset).add(0, virtualYOffset, 0);
			BlockState blockState = protoRegion.getBlockState(sourcePos);
			if (!blockState.isAir()) {
				world.setBlockState(blockPos, protoRegion.getBlockState(blockPos.add(pocketOriginChunkOffset).add(0, virtualYOffset, 0)));
			}
		}
		Box realBox = new Box(firstCorner, secondCorner);
		for (Chunk protoChunk : protoChunks) {
			for(BlockPos virtualBlockPos : protoChunk.getBlockEntityPositions()) {
				BlockPos realBlockPos = virtualBlockPos.subtract(pocketOriginChunkOffset).add(0, -virtualYOffset, 0);
				if (realBox.contains(realBlockPos.getX(), realBlockPos.getY(), realBlockPos.getZ())) {
					world.addBlockEntity(protoChunk.getBlockEntity(virtualBlockPos)); // TODO: ensure this works, likely bugged
				}
			}
		}
		Box virtualBox = realBox.offset(pocketOriginChunkOffset.add(0, virtualYOffset, 0));
		/*
		for (Entity entity : protoRegion.getOtherEntities(null, virtualBox)) { // TODO: does this even work?
			TeleportUtil.teleport(entity, world, entity.getPos().add(-pocketOriginChunkOffset.getX(), -pocketOriginChunkOffset.getY() - virtualYOffset, -pocketOriginChunkOffset.getZ()), entity.yaw);
		} // TODO: Entities?/ Biomes/ Structure Data
		*/
		world.setBlockState(world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pocket.getOrigin()), ModBlocks.DETACHED_RIFT.getDefaultState());

		DetachedRiftBlockEntity rift = ModBlockEntityTypes.DETACHED_RIFT.instantiate(world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pocket.getOrigin()), ModBlocks.DETACHED_RIFT.getDefaultState());
		world.addBlockEntity(rift);

		rift.setDestination(new PocketEntranceMarker());
		pocket.virtualLocation = sourceVirtualLocation;

		return pocket;
	}

	@Override
	public PocketGeneratorType<? extends PocketGenerator> getType() {
		return PocketGeneratorType.CHUNK;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Vec3i getSize(PocketGenerationParameters parameters) {
		return size;
	}

	private static class ChunkRegionHack extends ChunkRegion { // Please someone tell me if there is a better way
		ChunkRegionHack(ServerWorld world, List<Chunk> chunks) {
			super(world, chunks);
		}

		@Override
		public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
			Chunk chunk = super.getChunk(chunkX, chunkZ, leastStatus, false);
			return chunk == null ? new ProtoChunkHack(new ChunkPos(chunkX, chunkZ), UpgradeData.NO_UPGRADE_DATA, this) : chunk;
		}

		// TODO: Override getSeed()
	}

	private static class ProtoChunkHack extends ProtoChunk { // exists solely to make some calls in the non utilized chunks faster
		public ProtoChunkHack(ChunkPos pos, UpgradeData upgradeData, HeightLimitView world) {
			super(pos, upgradeData, world);
		}

		@Override
		public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
			return Blocks.VOID_AIR.getDefaultState();
		}

		@Override
		public BlockState getBlockState(BlockPos pos) {
			return Blocks.VOID_AIR.getDefaultState();
		}

		@Override
		public FluidState getFluidState(BlockPos pos) {
			return Fluids.EMPTY.getDefaultState();
		}
	}
}
